package edu.sjsu.cmpe.library.api.resources;

import java.util.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.yammer.metrics.annotation.Timed;

import edu.sjsu.cmpe.library.dto.*;
import edu.sjsu.cmpe.library.domain.*;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/v1/books")
public class BookResource {

	public BookResource() {
		// do nothing
	}

	/**
	 * This function List all the books with links and action can be performed
	 * @return List of all books
	 */
	@GET
	@Timed(name = "list-book")
	public Response getBooks() {

		BooksDto booksListResponse = new BooksDto();
		try{
			for(Iterator<Book> i = BooksDto.getBooks().iterator(); i.hasNext(); ) {
				Book iBook = i.next();
				booksListResponse.addLink(new LinkDto("view-book", "/books/" + iBook.getIsbn(),
						"GET"));
				booksListResponse.addLink(new LinkDto("update-book",
						"/books/" + iBook.getIsbn(), "PUT"));
				booksListResponse.addLink(new LinkDto("delete-book",
						"/books/" + iBook.getIsbn(), "DELETE"));
				booksListResponse.addLink(new LinkDto("create-review",
						"/books/" + iBook.getIsbn() + "/reviews", "POST"));
			}

			return Response.status(200).entity(booksListResponse).build();
		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
			if(booksListResponse != null)
			{
				booksListResponse = null;
			}
		}

	}


	/**
	 * This function will create and Add Book Entry to List of Books
	 * @param book
	 */
	@POST
	@Timed(name="create-book")
	public Response createBook(@Valid Book bookToCreate) {
		BooksDto bookCreatedResponse = new BooksDto();
		Book bookSavedResponse = BooksDto.addBookToStorage(bookToCreate);

		try{

			if(bookSavedResponse.getIsbn() >0)
			{
				bookCreatedResponse.addLink(new LinkDto("view-book", "/books/" + bookSavedResponse.getIsbn(),
						"GET"));
				bookCreatedResponse.addLink(new LinkDto("update-book",
						"/books/" + bookSavedResponse.getIsbn(), "PUT"));
				bookCreatedResponse.addLink(new LinkDto("delete-book",
						"/books/" + bookSavedResponse.getIsbn(), "DELETE"));
				bookCreatedResponse.addLink(new LinkDto("create-review",
						"/books/" + bookSavedResponse.getIsbn() + "/reviews/", "POST"));
			}

			return Response.status(201).entity(bookCreatedResponse).build();


		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
			if(bookCreatedResponse != null)
			{
				bookCreatedResponse = null;
			}
			if(bookSavedResponse != null)
			{
				bookSavedResponse = null;
			}

		}
	}

	/**
	 * This function is responsible to get a single book based on ISBN
	 * @param isbn
	 * @return
	 */
	@GET
	@Path("/{isbn}")
	@Timed(name = "view-book")
	public Response getBookByIsbn(@PathParam("isbn") int isbn) {
		BookDto bookResponse = new BookDto(BooksDto.getBookByISBN(isbn));
		try{

			bookResponse.addLink(new LinkDto("view-book", "/books/" + bookResponse.getBook().getIsbn(),
					"GET"));
			bookResponse.addLink(new LinkDto("update-book",
					"/books/" + bookResponse.getBook().getIsbn(), "PUT"));
			bookResponse.addLink(new LinkDto("delete-book",
					"/books/" +  bookResponse.getBook().getIsbn(), "DELETE"));
			bookResponse.addLink(new LinkDto("create-review",
					"/books/" +  bookResponse.getBook().getIsbn() + "/reviews/", "POST"));

			if(bookResponse.getBook().getBookRating()!= null && !bookResponse.getBook().getBookRating().isEmpty())
			{
				bookResponse.addLink(new LinkDto("view-all-reviews",
						"/books/" +  bookResponse.getBook().getIsbn() + "/reviews/", "GET"));
			}

			return Response.status(200).entity(bookResponse).build();

		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
			if(bookResponse != null)
			{
				bookResponse = null;
			}


		}
	}

	@DELETE
	@Path("/{isbn}")
	@Timed(name = "delete-book")
	public Response deleteBookByIsbn(@PathParam("isbn") int isbn){

		BooksDto bookDeleteResponse = new BooksDto();
		try{
			BooksDto.deleteBookByISBN(isbn);

			bookDeleteResponse.addLink(new LinkDto("create-book",
					"/books", "POST"));

			return Response.status(200).entity(bookDeleteResponse).build();
		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
			if(bookDeleteResponse != null)
			{
				bookDeleteResponse = null;
			}
		}

	}

	@PUT
	@Path("/{isbn}")
	@Timed(name = "update-book")
	public Response updateookByIsbn(@PathParam("isbn") int isbn,@QueryParam("status") String status){

		BookDto updatedBookResponse = new BookDto(BooksDto.updateBookByISBN(isbn, status));
		try{

			updatedBookResponse.addLink(new LinkDto("view-book", "/books/" + updatedBookResponse.getBook().getIsbn(),
					"GET"));
			updatedBookResponse.addLink(new LinkDto("update-book",
					"/books/" + updatedBookResponse.getBook().getIsbn(), "PUT"));
			updatedBookResponse.addLink(new LinkDto("delete-book",
					"/books/" + updatedBookResponse.getBook().getIsbn(), "DELETE"));
			updatedBookResponse.addLink(new LinkDto("create-reviews",
					"/books/" + updatedBookResponse.getBook().getIsbn() + "/reviews/", "POST"));

			if(updatedBookResponse.getBook().getBookRating()!= null && !updatedBookResponse.getBook().getBookRating().isEmpty())
			{
				updatedBookResponse.addLink(new LinkDto("create-reviews",
						"/books/" + updatedBookResponse.getBook().getIsbn() + "/reviews/", "GET"));
			}

			return Response.status(200).entity(updatedBookResponse).build();
		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
			if(updatedBookResponse != null)
			{
				updatedBookResponse = null;
			}
		}
	}

	@GET
	@Path("/{isbn}/reviews")
	@Timed(name = "view-book")
	public /*List<Review>*/ Response getBookReviews(@PathParam("isbn") int isbn) {
		try{
			return Response.status(200).entity(BooksDto.getReviews(isbn)).build();
		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
		}
	}

	@POST
	@Path("/{isbn}/reviews")
	@Timed(name = "create-review")
	public Response createBookReviews(@PathParam("isbn") int isbn,@Valid Review review) {

		Review reviewResponse =  BooksDto.createReviews(isbn, review);		
		BooksDto bookCreateReviewResponse = new BooksDto();
		try{

			bookCreateReviewResponse.addLink(new LinkDto("view-review", "/books/"+isbn+"/reviews/"+reviewResponse.getId() ,
					"GET"));

			return Response.status(201).entity(bookCreateReviewResponse).build();
		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
			if(reviewResponse != null)
			{
				reviewResponse = null;
			}
			if(bookCreateReviewResponse != null)
			{
				bookCreateReviewResponse = null;
			}
		}
	}

	@GET
	@Path("/{isbn}/reviews/{id}")
	@Timed(name = "view-review")
	public Response getBookReviewsByIsbn(@PathParam("isbn") int isbn, @PathParam("id") int reviewId) {

		Review reviewResponse = BooksDto.getReviewsByISBN(isbn, reviewId);
		try{
			reviewResponse.addLink(new LinkDto("view-review", "/books/"+isbn+"/reviews/"+reviewResponse.getId() ,
					"GET"));
			return Response.status(200).entity(reviewResponse).build();
		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
			if(reviewResponse != null)
			{
				reviewResponse = null;
			}
		}
	}

	@GET
	@Path("/{isbn}/authors")
	@Timed(name = "view-authors")
	public Response/*List<Author>*/ getBookAuthors(@PathParam("isbn") int isbn) {

		try{
			return Response.status(200).entity(BooksDto.getAuthors(isbn)).build();
		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
		}
	}

	@GET
	@Path("/{isbn}/authors/{id}")
	@Timed(name = "view-author")
	public Response getBookAuthorsByIsbn(@PathParam("isbn") int isbn, @PathParam("id") int authId) {

		Author authorResponse =  BooksDto.getAuthorsByISBN(isbn, authId);
		try{

			authorResponse.addLink(new LinkDto("view-review", "/books/"+isbn+"/reviews/"+authorResponse.getAuthorId() ,
					"GET"));

			return Response.status(200).entity(authorResponse).build();
		}
		catch(Exception ex)
		{
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Parameters " + ex.getMessage()).build();
		}
		finally
		{
			if(authorResponse!= null)
			{
				authorResponse= null;
			}
		}
	}
}



