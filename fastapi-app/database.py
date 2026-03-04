import ssl
import pymysql
from datetime import datetime, date

# 🔐 DB 접속 정보 (여기에 직접 입력)
DB_HOST='gateway01.ap-northeast-1.prod.aws.tidbcloud.com'
DB_USER='2U4xYtkqfE9KuPV.root'
DB_PASSWORD='jAlb5pARLRdZGKLe'
DB_NAME='test'
DB_PORT='4000'


# 🔌 DB 연결 함수
def get_connection():
    return pymysql.connect(
        host=DB_HOST,
        port=DB_PORT,
        user=DB_USER,
        password=DB_PASSWORD,
        db=DB_NAME,
        charset="utf8mb4",
        cursorclass=pymysql.cursors.DictCursor,
        ssl={"check_hostname": False, "verify_mode": ssl.CERT_NONE},
    )


def _to_date(v):
    if not v:
        return None
    if isinstance(v, date):
        return v
    if isinstance(v, datetime):
        return v.date()
    if isinstance(v, str):
        try:
            return datetime.strptime(v[:10], "%Y-%m-%d").date()
        except Exception:
            return None
    return None


# 🏗️ 테이블 초기화
def init_db():
    conn = get_connection()
    try:
        with conn.cursor() as cursor:
            sql = """
            CREATE TABLE IF NOT EXISTS event (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255),
                place_name VARCHAR(255),
                address VARCHAR(255),
                area VARCHAR(500),
                lat DOUBLE,
                lng DOUBLE,
                start_date DATE,
                end_date DATE,
                image_url TEXT,
                category VARCHAR(50),
                source VARCHAR(50),
                org_link TEXT,
                use_fee VARCHAR(255),
                hashtag VARCHAR(500),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                UNIQUE KEY uk_event_title_place (title, place_name),
                INDEX idx_event_title (title),
                INDEX idx_event_place (place_name)
            );
            """
            cursor.execute(sql)

        conn.commit()
        print("✅ DB 테이블(event) 초기화 완료!")

    except Exception as e:
        print(f"❌ 테이블 생성 실패: {e}")

    finally:
        conn.close()


# 💾 데이터 저장
def save_event(data: dict):
    conn = get_connection()
    try:
        with conn.cursor() as cursor:

            sql = """
            INSERT INTO event (
                title, place_name, address, area,
                lat, lng, start_date, end_date,
                image_url, category, source, org_link,
                use_fee, hashtag
            )
            VALUES (
                %s,%s,%s,%s,
                %s,%s,%s,%s,
                %s,%s,%s,%s,
                %s,%s
            )
            ON DUPLICATE KEY UPDATE
                address=VALUES(address),
                area=VALUES(area),
                lat=VALUES(lat),
                lng=VALUES(lng),
                start_date=VALUES(start_date),
                end_date=VALUES(end_date),
                image_url=VALUES(image_url),
                category=VALUES(category),
                source=VALUES(source),
                org_link=VALUES(org_link),
                use_fee=VALUES(use_fee),
                hashtag=VALUES(hashtag);
            """

            cursor.execute(sql, (
                data.get("title"),
                data.get("place_name"),
                data.get("address"),
                data.get("area"),

                data.get("lat"),
                data.get("lng"),
                _to_date(data.get("start_date")),
                _to_date(data.get("end_date")),

                data.get("image_url"),
                data.get("category"),
                data.get("source"),
                data.get("org_link"),

                data.get("use_fee"),
                data.get("hashtag")
            ))

        conn.commit()

    except Exception as e:
        print(f"❌ 저장 에러 ({data.get('title')}): {e}")

    finally:
        conn.close()