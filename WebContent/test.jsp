<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<div>
	<form class="" method="post" action="DirectoriApi" enctype="multipart/form-data">
		<select name="action">
		             
        			<option value="addFriend">addFriend (token,friendname)</option>
        			<option value="getMyGalleries">getMyGalleries(token)</option>
        			<option value="getFriendGalleries">getFriendGalleries(token,friendname)</option>
        			<option value="createGallery">createGallery(token,galleryname)</option>
        			<option value="deleteImage">deleteImage(token,imageid)</option>
        			<option value="deleteFriend">deleteFriend(token,friendname)</option>
        			<option value ="getFriends">getFriends(token)</option>
        			<option value ="deleteGallery">deleteGallery(token,galleryname)</option>
        			<option value="postComment">postComment(token, imageid, comment)</option>
        			<option value="getGallery">getGallery(token, galleryid)</option>
        			<option value="deleteGallery">deleteGallery(token, galleryname)</option>
        			<option value="postImage">postimage(token, file)</option>
        		</select>
        		<div class="row username">
        					token
	    			<input type="text" id="token" name="token" />
        		</div>
        		
        		<div class="row pass">
        		friendname
        			<input type="text" id="friendname" name="friendname" />
        		<div class="row pass">
        		galleryname
        			<input type="text" id="friendname" name="galleryname" />
        		</div>
        		</div>
        		<div class="row username">
        					imageid
	    			<input type="text" id="imageid" name="imageid" />
        		</div>
        		<div class="row username">
        					comment
	    			<input type="text" id="comment" name="comment" />
        		</div>
        		<div class="row username">
        					galleryid
	    			<input type="text" id="galleryid" name="galleryid" />
        		</div>
        		<div>
        		     imagefile
        			<input type="file" name="file" />
        		</div>
        		
     
        		
        	
        		<input type="submit" value="Login" />

	</form>
</div>

</body>
</html>