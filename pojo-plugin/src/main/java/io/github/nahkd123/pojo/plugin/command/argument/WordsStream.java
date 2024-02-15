package io.github.nahkd123.pojo.plugin.command.argument;

public class WordsStream {
	private String[] array;
	private int position;

	public WordsStream(String[] array, int position) {
		this.array = array;
		this.position = position;
	}

	public String[] getArray() { return array; }

	public int getPosition() { return position; }

	public String peek() {
		return array[position];
	}

	public String next() {
		return array[position++];
	}

	public boolean isAvailable() { return position < array.length; }

	public WordsStream fork() {
		return new WordsStream(array, position);
	}
}
