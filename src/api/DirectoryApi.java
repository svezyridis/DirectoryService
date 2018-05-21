package api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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
import crypto.Token;
import images.Comments;
import images.Gallery;
import images.Image;
import zookeeper.Configuration;

/**
 * Servlet implementation class DirectoriApi
 */
@WebServlet("/DirectoriApi")
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		response.setContentType("application/json;charset=UTF-8");
		
		String JSONString=request.getParameter("token");
		if (JSONString.equals("")) {
			PrintWriter out = response.getWriter();
			JSONObject resJSON = new JSONObject();
			resJSON.put("error","no token found");
			out.print(resJSON);
			out.flush();
			return;
		}
		
		
		JSONObject token=new JSONObject(JSONString);
		String cryptedJSONString= token.getString("crypted");
		// Decrypt token
		try {
			token = Token.getDecryptedToken(cryptedJSONString);
		} catch (GeneralSecurityException e) {
			PrintWriter out = response.getWriter();
			JSONObject resJSON = new JSONObject();
			resJSON.put("error","token could not be decrypted");
			out.print(resJSON);
			out.flush();
			return;
		}
		
		String username=CheckNewUser.checkIfnewAndadd(token);
		String action=request.getParameter("action");
		
		if (action.equals("addFriend")){
			PrintWriter out = response.getWriter();
			String friend=request.getParameter("friendname");
			JSONObject resJSON = Friends.addFriend(username, friend);
			out.print(resJSON);
			out.flush();
			return;
		}
		else if(action.equals("deleteFriend")) {
			PrintWriter out = response.getWriter();
			String friend=request.getParameter("friendname");
			JSONObject resJSON = Friends.deleteFriend(username, friend);
			out.print(resJSON);
			out.flush();
			return;	
		}
		else if(action.equals("getFriends")) {
			PrintWriter out = response.getWriter();
			JSONObject resJSON = Friends.getFriends(username);
			out.print(resJSON);
			out.flush();
			return;	
		}
		else if(action.equals("getMyGalleries")) {
			PrintWriter out = response.getWriter();
			JSONObject resJSON = Gallery.getUserGalleries(username);
			out.print(resJSON);
			out.flush();
			return;	
		}
		else if(action.equals("getFriendGalleries")) {
			PrintWriter out = response.getWriter();
			String friendname =request.getParameter("friendname");
			JSONObject resJSON = Gallery.getFriendGalleries(username, friendname);
			out.print(resJSON);
			out.flush();
			return;	
		}
		else if(action.equals("createGallery")) {
			PrintWriter out = response.getWriter();
			String galleryname=request.getParameter("galleryname");
			JSONObject resJSON = Gallery.createGallery(username, galleryname);
			out.print(resJSON);
			out.flush();
			return;
			
		}
		else if(action.equals("deleteGallery")) {
			PrintWriter out = response.getWriter();
			String galleryname=request.getParameter("galleryname");
			JSONObject resJSON = Gallery.deleteGallery(username, galleryname);
			out.print(resJSON);
			out.flush();
			return;
			
		}
		else if(action.equals("deleteImage")) {
			PrintWriter out = response.getWriter();
			String imageid=request.getParameter("imageid");
			JSONObject resJSON = Image.deleteImage(username, imageid);
			out.print(resJSON);
			out.flush();
			return;
			
		}
		else if(action.equals("postComment")) {
			PrintWriter out = response.getWriter();
			String imageid=request.getParameter("imageid");
			String comment=request.getParameter("comment");			
			JSONObject resJSON = Comments.postComment(username,imageid,comment);
			out.print(resJSON);
			out.flush();
			return;		
		}
		else if(action.equals("getGallery")) {
			PrintWriter out = response.getWriter();
			String galleryid=request.getParameter("galleryid");			
			JSONObject resJSON = Image.getImages(username, galleryid);
			out.print(resJSON);
			out.flush();
			return;		
		}
		else if(action.equals("postImage")) {
			JSONObject resJSON =Image.postImage(username, request);
			PrintWriter out = response.getWriter();
			out.print(resJSON);
			out.flush();
			int i=0;
			
			/*while(i<30) {
			        if (resJSON!=null) {
			            break;
			        } else {
			            try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            ++i;
			            if (i == 30) {
			                try {
								throw new TimeoutException("Timed out after waiting for " + i + " seconds");
							} catch (TimeoutException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			            }
			
			       }	
		    }*/
			return;
		}
		else if(action.equals("test")) {
			String nonce=request.getParameter("comment");
			Configuration instance=zookeeper.Configuration.getInstance();
			instance.AddNode(nonce);
		
	
		}
			
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
