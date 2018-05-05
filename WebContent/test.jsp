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
	<form class="" method="post" action="DirectoriApi">
		<select name="action">
        			<option>addFriend</option>
        			<option>getMyGalleries</option>
        			<option>getFriendGalleries</option>
        			<option>createGallery</option>
        			<option>deleteImage</option>
        			<option>deleteFriend</option>
        			<option>deleteGallery</option>
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
        		
     
        		
        	
        		<input type="submit" value="Login" />

	</form>
</div>

</body>
</html>