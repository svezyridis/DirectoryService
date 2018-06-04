package api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.servlet.jsp.PageContext;

import org.json.JSONObject;

import crypto.Encryption;
import crypto.Token;
import crypto.Validator;
import images.Comments;
import images.Gallery;
import images.Image;
import zookeeper.Configuration;

/**
 * Servlet implementation class DirectoriApi
 */
@WebServlet("/DirectoryApi")
@MultipartConfig
public class DirectoryApi extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DirectoryApi() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		response.setContentType("application/json;charset=UTF-8");

		String JSONString = request.getParameter("token");
		if (JSONString.equals("")) {

			JSONObject resJSON = new JSONObject();
			resJSON.put("error", "no token found");
			out.print(resJSON);
			out.flush();
			return;
		}

		JSONObject reqtoken = new JSONObject(JSONString);
		JSONObject token = new JSONObject();
		String cryptedJSONString = reqtoken.getString("crypted");
		// Decrypt token
		try {
			token = Token.getDecryptedToken(cryptedJSONString,reqtoken.getString("issuer"));
		} catch (GeneralSecurityException e) {

			JSONObject resJSON = new JSONObject();
			resJSON.put("error", "token could not be decrypted");
			out.print(resJSON);
			out.flush();
			return;
		} catch (IllegalArgumentException e) {

			JSONObject resJSON = new JSONObject();
			resJSON.put("error", "token could not be decrypted");
			out.print(resJSON);
			out.flush();
			return;
		} catch (UnsupportedEncodingException e) {

			JSONObject resJSON = new JSONObject();
			resJSON.put("error", "token could not be decrypted");
			out.print(resJSON);
			out.flush();
			return;
		}

		int validtill = token.getInt("validtill");
		if (!Validator.validatetime(validtill)) {
			JSONObject resJSON = new JSONObject();
			resJSON.put("error", "token has expired");
			out.print(resJSON);
			out.flush();
			return;
		}
		String username = CheckNewUser.checkIfnewAndadd(token);
		String action = request.getParameter("action");

		if (action.equals("addFriend")) {

			String friend = request.getParameter("friendname");
			JSONObject resJSON = Friends.addFriend(username, friend);
			out.print(resJSON);
			out.flush();
			return;
		} else if (action.equals("deleteFriend")) {

			String friend = request.getParameter("friendname");
			JSONObject resJSON = Friends.deleteFriend(username, friend);
			out.print(resJSON);
			out.flush();
			return;
		} else if (action.equals("getUsername")) {
			JSONObject resJSON = new JSONObject();
			resJSON.put("username", username);
			resJSON.put("error", "");
			out.print(resJSON);
			out.flush();
			return;
		} else if (action.equals("getFriends")) {

			JSONObject resJSON = Friends.getFriends(username);
			out.print(resJSON);
			out.flush();
			return;
		} else if (action.equals("getMyGalleries")) {

			JSONObject resJSON = Gallery.getUserGalleries(username);
			out.print(resJSON);
			out.flush();
			return;
		} else if (action.equals("getFriendGalleries")) {

			String friendname = request.getParameter("friendname");
			JSONObject resJSON = Gallery.getFriendGalleries(username, friendname);
			out.print(resJSON);
			out.flush();
			return;
		} else if (action.equals("createGallery")) {

			String galleryname = request.getParameter("galleryname");
			JSONObject resJSON = Gallery.createGallery(username, galleryname);
			out.print(resJSON);
			out.flush();
			return;

		} else if (action.equals("deleteGallery")) {

			String galleryname = request.getParameter("galleryname");
			JSONObject resJSON = Gallery.deleteGallery(username, galleryname);
			out.print(resJSON);
			out.flush();
			return;

		} else if (action.equals("deleteImage")) {

			String imageid = request.getParameter("imageid");
			JSONObject resJSON = Image.deleteImage(username, imageid);
			out.print(resJSON);
			out.flush();
			return;

		} else if (action.equals("postComment")) {

			String imageid = request.getParameter("imageid");
			String comment = request.getParameter("comment");
			JSONObject resJSON = Comments.postComment(username, imageid, comment);
			out.print(resJSON);
			out.flush();
			return;
		} else if (action.equals("getGallery")) {

			String galleryid = request.getParameter("galleryid");
			JSONObject resJSON = Image.getImages(username, galleryid);
			resJSON.put("owner", Database.getUsername((Gallery.getOwner(Integer.parseInt(galleryid)))));
			out.print(resJSON);
			out.flush();
			return;
		} else if (action.equals("getComments")) {

			String imageid = request.getParameter("imageid");
			JSONObject resJSON = Image.getComments(username, imageid);
			out.print(resJSON);
			out.flush();
			return;
		} else if (action.equals("postImage")) {
			JSONObject resJSON = Image.postImage(username, request);
			out.print(resJSON);
			out.flush();
			int i = 0;

			/*
			 * while(i<30) { if (resJSON!=null) { break; } else { try {
			 * TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } ++i; if (i == 30) { try {
			 * throw new TimeoutException("Timed out after waiting for " + i + " seconds");
			 * } catch (TimeoutException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } }
			 * 
			 * } }
			 */
			return;
		}

		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
