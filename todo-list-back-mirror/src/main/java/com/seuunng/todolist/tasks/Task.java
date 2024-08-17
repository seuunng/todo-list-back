package com.seuunng.todolist.tasks;

public class Task {
	  private Long no;
	    private String title;
	    private String content;

	    public Task(Long no, String title, String content) {
	        this.no = no;
	        this.title = title;
	        this.content = content;
	    }

	    // Getters and setters
	    public Long getNo() {
	        return no;
	    }

	    public void setNo(Long no) {
	        this.no = no;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }

	    public String getContent() {
	        return content;
	    }

	    public void setContent(String content) {
	        this.content = content;
	    }
}
