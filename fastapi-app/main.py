# main.py
from fastapi import FastAPI, File, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import HTMLResponse
from pydantic import BaseModel
import pymysql
import os
import uvicorn
import base64

from database import get_connection
from ai_service import recommend_exhibitions, generate_multilingual_docent, generate_course_text_v3

app = FastAPI(title="ArtLog API Server")

origins = [
    "http://54.180.234.226:8000",      # (참고) 이건 FastAPI 자기 자신 origin이라 보통 의미는 적음
    "https://my-mobile-test.vercel.app",
    "http://localhost:5173",
    "http://localhost:5174",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

os.makedirs("audio", exist_ok=True)
app.mount("/audio", StaticFiles(directory="audio"), name="audio")

# ==========================================
# 📡 [API 1] 전체 전시 목록
# ==========================================
@app.get("/api/events")
def get_events():
    conn = get_connection()
    try:
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            sql = """
                SELECT
                    id,
                    title,
                    place_name,
                    address,
                    area,
                    lat,
                    lng,
                    start_date,
                    end_date,
                    image_url,
                    category,
                    source,
                    org_link,
                    use_fee,
                    hashtag
                FROM event
                WHERE lat IS NOT NULL
                  AND lng IS NOT NULL
                  AND lat <> 0
                  AND lng <> 0
            """
            events = cursor.fetchall()

        # ✅ 리스트(events)를 돌면서 각 row(dict)에 directions_url 추가
        for e in events:
            place = e.get("place_name") or "전시장"
            lat = e.get("lat")
            lng = e.get("lng")
            if lat and lng:
                e["directions_url"] = f"https://map.kakao.com/link/to/{place},{lat},{lng}"
            else:
                e["directions_url"] = ""

        return {"status": "success", "total": len(events), "data": events}
    finally:
        conn.close()

# ==========================================
# 🤖 [API 2] 취향 기반 전시 추천
# ==========================================
class RecommendReq(BaseModel):
    tags: list

@app.post("/api/ai/recommend")
def api_recommend(req: RecommendReq):
    conn = get_connection()
    try:
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            # ✅ directions_url 만들려면 lat/lng도 같이 SELECT 해야 함
            sql = """
                SELECT id, title, place_name, image_url, hashtag, lat, lng
                FROM event
                ORDER BY id DESC
                LIMIT 200
            """
            all_events = cursor.fetchall()

        results = recommend_exhibitions(req.tags, all_events)

        for item in results:
            place = item.get("place_name") or "전시장"
            lat = item.get("lat")
            lng = item.get("lng")
            if lat and lng:
                item["directions_url"] = f"https://map.kakao.com/link/to/{place},{lat},{lng}"
            else:
                item["directions_url"] = ""

        return {"status": "success", "data": results}
    finally:
        conn.close()

# ==========================================
# 🎤 [API 3] AI 도슨트 오디오 생성
# ==========================================
@app.post("/api/ai/docent")
async def api_docent(
    file: UploadFile = File(...),
    lang: str = Form(...)
):
    file_content = await file.read()
    base64_image = base64.b64encode(file_content).decode("utf-8")
    image_data = f"data:{file.content_type};base64,{base64_image}"

    result = generate_multilingual_docent(image_data, lang)
    return {"status": "success", "data": result}

# ==========================================
# 🗺️ [API 4] 나들이 코스 추천
# ==========================================
class CourseReq(BaseModel):
    destination: str
    who: str

@app.post("/api/ai/course")
def api_course(req: CourseReq):
    conn = get_connection()
    try:
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            sql = """
                SELECT id, title, place_name, lat, lng
                FROM event
                WHERE (place_name LIKE %s OR title LIKE %s)
                  AND lat IS NOT NULL AND lng IS NOT NULL
                  AND lat <> 0 AND lng <> 0
                ORDER BY start_date DESC, id DESC
                LIMIT 1
            """
            cursor.execute(sql, (f"%{req.destination}%", f"%{req.destination}%"))
            exhibition = cursor.fetchone()

        plan = generate_course_text_v3(req.destination, req.who, exhibition)

        if exhibition:
            place = exhibition.get("place_name") or "전시장"
            lat = exhibition.get("lat")
            lng = exhibition.get("lng")
            plan["directions_url"] = f"https://map.kakao.com/link/to/{place},{lat},{lng}"
        else:
            plan["directions_url"] = ""

        return {"status": "success", "data": plan}

    except Exception as e:
        print(f"❌ 코스 생성 중 에러 발생: {e}")
        return {"status": "error", "message": str(e)}
    finally:
        conn.close()

# ==========================================
# 🛠️ 카카오맵 테스트 화면
# ==========================================
KAKAO_JS_KEY = "1cc92d0b3666ef740a88e12a74a1fe06"

@app.get("/map", response_class=HTMLResponse)
def show_map():
    return f"""
    <!DOCTYPE html><html><head><meta charset="utf-8"/><title>지도 테스트</title>
    <style>body, html {{ margin: 0; height: 100%; }} #map {{ width: 100%; height: 100%; }}</style>
    </head><body><div id="map"></div>
    <script src="//dapi.kakao.com/v2/maps/sdk.js?appkey={KAKAO_JS_KEY}"></script>
    <script>
        var map = new kakao.maps.Map(document.getElementById('map'), {{center: new kakao.maps.LatLng(37.5665, 126.9780), level: 7}});
        // ✅ 여기 /api/event -> /api/events 로 수정
        fetch("/api/events").then(r => r.json()).then(res => {{
            res.data.forEach(evt => {{
                if (evt.lat && evt.lng) {{
                    var m = new kakao.maps.Marker({{ position: new kakao.maps.LatLng(evt.lat, evt.lng), map: map }});
                    var iw = new kakao.maps.InfoWindow({{ content : '<div style="padding:5px;"><b>'+evt.title+'</b></div>', removable : true }});
                    kakao.maps.event.addListener(m, 'click', () => iw.open(map, m));
                }}
            }});
        }});
    </script></body></html>
    """

# ==========================================
# ✅ DB 연결 테스트
# ==========================================
@app.get("/api/db-test")
def db_test():
    try:
        conn = get_connection()
        with conn.cursor() as cursor:
            cursor.execute("SELECT 1")
            result = cursor.fetchone()
        conn.close()
        return {"status": "success", "db": result}
    except Exception as e:
        return {"status": "error", "message": str(e)}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)