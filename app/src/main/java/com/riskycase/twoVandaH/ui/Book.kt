package com.riskycase.twoVandaH.ui

class Book() {
    private lateinit var id: String
    private lateinit var title: String
    private lateinit var author: String
    private lateinit var pages: String
    private lateinit var publishDate: String
    private lateinit var ISBN: String
    private lateinit var coverURL: String
    private lateinit var owner: String
    private lateinit var reader: String
    
    constructor(id: String, title: String, author: String, pages: String, publishDate: String, ISBN: String, coverURL: String, owner: String, reader: String) : this() {
        this.id = id
        this.title = title
        this.author = author
        this.pages = pages
        this.publishDate = publishDate
        this.ISBN = ISBN
        this.coverURL = coverURL
        this.owner = owner
        this.reader = reader
    }
    
    constructor(id: String) : this() {
        this.id = id
    }

    fun setId(id: String): Book {
        this.id = id
        return this
    }

    fun getId(): String {
        return this.id
    }

    fun setTitle(title: String): Book {
        this.title = title
        return this
    }

    fun getTitle(): String {
        return this.title
    }

    fun setAuthor(author: String): Book {
        this.author = author
        return this
    }

    fun getAuthor(): String {
        return this.author
    }

    fun setPages(pages: String): Book {
        this.pages = pages
        return this
    }

    fun getPages(): String {
        return this.pages
    }

    fun setPublishDate(publishDate: String): Book {
        this.publishDate = publishDate
        return this
    }

    fun getPublishDate(): String {
        return this.publishDate
    }

    fun setISBN(ISBN: String): Book {
        this.ISBN = ISBN
        return this
    }

    fun getISBN(): String {
        return this.ISBN
    }

    fun setCoverURL(coverURL: String): Book {
        this.coverURL = coverURL
        return this
    }

    fun getCoverURL(): String {
        return this.coverURL
    }

    fun setOwner(owner: String): Book {
        this.owner = owner
        return this
    }

    fun getOwner(): String {
        return this.owner
    }

    fun setReader(reader: String): Book {
        this.reader = reader
        return this
    }

    fun getReader(): String {
        return this.reader
    }
    
}
