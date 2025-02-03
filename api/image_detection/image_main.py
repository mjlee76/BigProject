from fastapi import FastAPI, File, UploadFile, HTTPException
import nsfw_detection as nd
import shutil
import os

app = FastAPI()

# 저장할 디렉토리 설정
UPLOAD_DIR = "uploaded_images"
os.makedirs(UPLOAD_DIR, exist_ok=True)

load_directory = "./nsfw_model"
model, processor, classifier = nd.load_model(load_directory)

@app.post("/upload/")
async def upload_image(file: UploadFile = File(...)):

    # 이미지 타입 검사
    if not file.filename.lower().endswith((".jpg", ".jpeg", ".png", ".gif", ".bmp")):
        raise HTTPException(status_code=400, detail="이미지 파일만 업로드할 수 있습니다.")
    
    # MIME 타입 추가 검사
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="유효한 이미지 파일이 아닙니다.")
    
    try:
        file_location = f"{UPLOAD_DIR}/{file.filename}"
        
        with open(file_location, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
        image = nd.load_image(UPLOAD_DIR)
        
        # 이미지가 손상되었는지 체크
        try : 
            image.verify()
            
        except Exception as e:
            raise HTTPException(status_code=400, detail="이미지 파일이 손상되었거나 유효하지 않습니다.")
        
        result = classifier(image)
        nsfw_score = None
        for item in result:
            if item.get("label") == "nsfw":
                nsfw_score = item.get("score")
                break
        if nsfw_score is not None and nsfw_score > 0.7:
            os.remove(file_location)
            raise HTTPException(status_code=400, detail="NSFW 이미지로 판단되어 업로드가 차단되었습니다.")

        return {"message": "Success", "result": result}

    except Exception as e:
        return {"error": str(e)}
    
    '''finally:
        # 업로드 디렉토리 내의 모든 파일 삭제
        files = glob.glob(os.path.join(UPLOAD_DIR, "*.*"))
        for f in files:
            try:
                os.remove(f)
            except Exception as ex:
                print(f"Error deleting file {f}: {ex}")'''