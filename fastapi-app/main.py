from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.responses import JSONResponse

app = FastAPI()

# ✅ GET 테스트용
@app.get("/ai/test")
def test():
    return JSONResponse(
        content={"message": "FastAPI OK"},
        media_type="application/json"
    )


# ✅ 요청 DTO
class AskRequest(BaseModel):
    question: str


# ✅ POST 질문 처리
@app.post("/ai/ask")
def ask(req: AskRequest):
    answer_text = f"받은 질문: {req.question}"

    return JSONResponse(
        content={"answer": answer_text},
        media_type="application/json"
    )