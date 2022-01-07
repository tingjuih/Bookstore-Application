package business.book;

public class Book {
	private final long BOOK_ID;
	private final String TITLE;
	private final String AUTHOR;
	private final String DESCRIPTION;
	private final int PRICE;
	private final int RATING;
	private final boolean IS_PUBLIC;
	private final boolean IS_FEATURED;
	private final long CATEGORY_ID;

	public Book(long BOOK_ID, String TITLE, String AUTHOR, String DESCRIPTION, int PRICE,
				int RATING, boolean IS_PUBLIC, boolean IS_FEATURED, long CATEGORY_ID) {
		this.BOOK_ID = BOOK_ID;
		this.TITLE = TITLE;
		this.AUTHOR = AUTHOR;
		this.DESCRIPTION = DESCRIPTION;
		this.PRICE = PRICE;
		this.RATING = RATING;
		this.IS_PUBLIC = IS_PUBLIC;
		this.IS_FEATURED = IS_FEATURED;
		this.CATEGORY_ID = CATEGORY_ID;
	}

	public long getBookId() {
		return BOOK_ID;
	}

	public String getTitle() {
		return TITLE;
	}

	public String getAuthor() {
		return AUTHOR;
	}

	public String getDescription() {
		return DESCRIPTION;
	}

	public int getPrice() {
		return PRICE;
	}

	public int getRating() {
		return RATING;
	}

	public boolean getIsPublic() {
		return IS_PUBLIC;
	}

	public boolean getIsFeatured() {
		return IS_FEATURED;
	}

	public long getCategoryId() {
		return CATEGORY_ID;
	}

	@Override
	public String toString() {
		return "Book{" +
				"BOOK_ID=" + BOOK_ID +
				", TITLE='" + TITLE + '\'' +
				", AUTHOR='" + AUTHOR + '\'' +
				", DESCRIPTION='" + DESCRIPTION + '\'' +
				", PRICE=" + PRICE +
				", RATING=" + RATING +
				", IS_PUBLIC=" + IS_PUBLIC +
				", IS_FEATURED=" + IS_FEATURED +
				", CATEGORY_ID=" + CATEGORY_ID +
				'}';
	}
}
