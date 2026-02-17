from fastapi import FastAPI
from pypdf import PdfReader
from pydantic import BaseModel
import os

app = FastAPI()

class FileRequest(BaseModel):
    file_path: str

@app.post("/extract-text")
def extract_text(request: FileRequest):
	
	file_path = request.file_path

	if not os.path.exists(file_path):
		raise HTTPException(status_code=404, detail="File not found")
	
	reader = PdfReader(file_path)
	text = ""
	
	for page in reader.pages:
		text += page.extract_text() or ""
		
	return {
        "pages": len(reader.pages),
        "characters": len(text),
        "preview": text
    }
