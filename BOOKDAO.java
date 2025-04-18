package pack;

import java.io.*;
import java.sql.*;
import java.util.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BOOKDAO extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        String id = request.getParameter("id");

        // Handle delete
        if ("delete".equals(action) && id != null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE id = ?");
                stmt.setInt(1, Integer.parseInt(id));
                stmt.executeUpdate();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect("BookServlet");
            return;
        }

        // Handle edit
        String[] editingBook = null;
        if ("edit".equals(action) && id != null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE id = ?");
                stmt.setInt(1, Integer.parseInt(id));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    editingBook = new String[]{
                        rs.getString("id"),
                        rs.getString("book_name"),
                        rs.getString("isbn"),
                        rs.getString("author")
                    };
                }
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Fetch all books
        List<String[]> books = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");
            while (rs.next()) {
                books.add(new String[]{
                    rs.getString("id"),
                    rs.getString("book_name"),
                    rs.getString("isbn"),
                    rs.getString("author")
                });
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Output HTML
        out.println("<!DOCTYPE html><html><head>");
        out.println("<meta charset='UTF-8'><title>SAWEN ONLINE LIBRARY</title>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("</head><body class='bg-light'>");

        out.println("<div class='container mt-5'>");
        out.println("<h2 class='text-center mb-4'>SAWEN ONLINE LIBRARY</h2>");

        out.println("<div class='card p-4 shadow'>");
        out.println("<form method='post' action='BookServlet'>");

        if (editingBook != null) {
            out.printf("<input type='hidden' name='editId' value='%s'/>", editingBook[0]);
        }

        out.println("<div class='mb-3'><label>Book Name</label>");
        out.printf("<input class='form-control' name='bookName' required value='%s'></div>",
                editingBook != null ? editingBook[1] : "");

        out.println("<div class='mb-3'><label>ISBN</label>");
        out.printf("<input class='form-control' name='isbn' required value='%s'></div>",
                editingBook != null ? editingBook[2] : "");

        out.println("<div class='mb-3'><label>Author</label>");
        out.printf("<input class='form-control' name='author' required value='%s'></div>",
                editingBook != null ? editingBook[3] : "");

        if (editingBook != null) {
            out.println("<button class='btn btn-primary w-100'>Update Book</button>");
        } else {
            out.println("<button class='btn btn-success w-100'>Submit</button>");
        }
        out.println("</form></div>");

        // Book list table (without ID column)
        out.println("<div class='mt-5'>");
        out.println("<h4>Saved Books</h4>");
        out.println("<table class='table table-bordered table-striped'>");
        out.println("<thead class='table-dark'><tr><th>Book Name</th><th>ISBN</th><th>Author</th><th>Actions</th></tr></thead><tbody>");
        for (String[] book : books) {
            out.printf("<tr><td>%s</td><td>%s</td><td>%s</td><td>" +
                    "<a href='BookServlet?action=edit&id=%s' class='btn btn-warning btn-sm me-2'>Edit</a>" +
                    "<a href='BookServlet?action=delete&id=%s' class='btn btn-danger btn-sm' onclick='return confirm(\"Delete this book?\")'>Delete</a>" +
                    "</td></tr>", book[1], book[2], book[3], book[0], book[0]);
        }
        out.println("</tbody></table></div>");

        out.println("</div></body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("editId");
        String bookName = request.getParameter("bookName");
        String isbn = request.getParameter("isbn");
        String author = request.getParameter("author");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            if (id != null && !id.isEmpty()) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE books SET book_name=?, isbn=?, author=? WHERE id=?");
                stmt.setString(1, bookName);
                stmt.setString(2, isbn);
                stmt.setString(3, author);
                stmt.setInt(4, Integer.parseInt(id));
                stmt.executeUpdate();
            } else {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (book_name, isbn, author) VALUES (?, ?, ?)");
                stmt.setString(1, bookName);
                stmt.setString(2, isbn);
                stmt.setString(3, author);
                stmt.executeUpdate();
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("BookServlet");
    }
}

