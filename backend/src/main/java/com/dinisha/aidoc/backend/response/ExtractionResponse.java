package com.dinisha.aidoc.backend.response;
public class ExtractionResponse {

    private int pages;
    private int characters;
    private String preview;
    private String error;
	public int getPages() {
		return pages;
	}
	public void setPages(int pages) {
		this.pages = pages;
	}
	public int getCharacters() {
		return characters;
	}
	public void setCharacters(int characters) {
		this.characters = characters;
	}
	public String getPreview() {
		return preview;
	}
	public void setPreview(String preview) {
		this.preview = preview;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

    
}
