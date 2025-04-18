package pack;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/BookServlet")
public class BookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // In-memory storage of books
    private static final List<Book> bookList = new ArrayList<>();

    static class Book {
        String name;
        String isbn;
        String author;

        Book(String name, String isbn, String author) {
            this.name = name;
            this.isbn = isbn;
            this.author = author;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("bookName");
        String isbn = request.getParameter("isbn");
        String author = request.getParameter("author");

        if (name != null && isbn != null && author != null &&
            !name.isEmpty() && !isbn.isEmpty() && !author.isEmpty()) {
            bookList.add(new Book(name, isbn, author));
        }

        doGet(request, response); // Redirect to display updated list
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("""
        <!DOCTYPE html>
        <html lang='en'>
        <head>
            <meta charset='UTF-8'>
            <meta name='viewport' content='width=device-width, initial-scale=1'>
            <title>SAWEN ONLINE LIBRARY</title>
            <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css' rel='stylesheet'>
            <style>
                body {
                    background: linear-gradient(to right, #e0f7fa, #fff3e0);
                    font-family: 'Segoe UI', sans-serif;
                }
                .header-title {
                    font-weight: bold;
                    color: #0d47a1;
                }
                .card {
                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                }
                .table thead {
                    background-color: #0d47a1;
                    color: white;
                }
            </style>
        </head>
        <body>
            <div class='container mt-5'>
                <div class='text-center mb-4'>
                    <h1 class='header-title'>SAWEN ONLINE LIBRARY</h1>
                    <p class='lead text-secondary'>Enter and view your book collection</p>
                </div>

                <div class='card mb-4'>
                    <div class='card-body'>
                        <form action='BookServlet' method='post'>
                            <div class='row g-3'>
                                <div class='col-md-4'>
                                    <label class='form-label'>Book Name</label>
                                    <input type='text' name='bookName' class='form-control' required>
                                </div>
                                <div class='col-md-4'>
                                    <label class='form-label'>ISBN</label>
                                    <input type='text' name='isbn' class='form-control' required>
                                </div>
                                <div class='col-md-4'>
                                    <label class='form-label'>Author</label>
                                    <input type='text' name='author' class='form-control' required>
                                </div>
                            </div>
                            <div class='mt-3 text-end'>
                                <button type='submit' class='btn btn-success'>Submit</button>
                                <button type='reset' class='btn btn-outline-danger'>Omit</button>
                            </div>
                        </form>
                    </div>
                </div>

                <div class='card'>
                    <div class='card-header bg-primary text-white'>Book List</div>
                    <div class='card-body p-0'>
                        <table class='table table-bordered table-hover m-0'>
                            <thead>
                                <tr>
                                    <th>Book Name</th>
                                    <th>ISBN</th>
                                    <th>Author</th>
                                </tr>
                            </thead>
                            <tbody>
        """);

        // Display each book in a table row
        for (Book book : bookList) {
            out.println("<tr>");
            out.println("<td>" + book.name + "</td>");
            out.println("<td>" + book.isbn + "</td>");
            out.println("<td>" + book.author + "</td>");
            out.println("</tr>");
        }

        if (bookList.isEmpty()) {
            out.println("<tr><td colspan='3' class='text-center text-muted'>No books added yet.</td></tr>");
        }

        out.println("""
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js'></script>
        </body>
        </html>
        """);
    }
}
