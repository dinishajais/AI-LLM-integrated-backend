from fastapi import FastAPI
from pypdf import PdfReader
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import os

app = FastAPI()
model = SentenceTransformer("all-MiniLM-L6-v2")

class FileRequest(BaseModel):
    file_path: str
    
class EmbedRequest(BaseModel):
    text: str

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
@app.post("/embed")
def embed_text(request: EmbedRequest):
	text=request.text
	embedding = model.encode(text).tolist()
	return {
		"embedding": embedding
    }
