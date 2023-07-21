package org.example.DTO;

public class ReceivedMessage {
    private String word;
    private Integer count;

    public ReceivedMessage() {}

    public ReceivedMessage(String word, Integer count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void increaseCount(Integer c) {
        this.count += c;
    }
}
