package io.khaminfo.askmore.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.Principal;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.model.File;

import io.khaminfo.askmore.domain.Crop;
import io.khaminfo.askmore.domain.User;
import io.khaminfo.askmore.domain.UserInfo;
import io.khaminfo.askmore.exceptions.AccessException;
import io.khaminfo.askmore.repositories.ProfileRepository;
import io.khaminfo.askmore.repositories.UserRepository;

@Service
public class ProfileService {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProfileRepository profileRepository;
	public void changePassword(String oldPassword , String newPassword , Principal principal) {
		
		
		newPassword = bCryptPasswordEncoder.encode(newPassword);
		oldPassword = bCryptPasswordEncoder.encode(oldPassword);
	
		User user = userRepository.findByUsername(principal.getName());
		if(bCryptPasswordEncoder.matches(user.getPassword(),oldPassword)) {
			throw new AccessException("wrong password.");
		}
		
		if(userRepository.updateUserPassword(newPassword, principal.getName())!=1)
		{
			throw new AccessException("Wrong password!!");
		}
		
	}
	
	
	public UserInfo updateInfo(UserInfo userInfo,Principal principal) {
	
		 UserInfo info = profileRepository.findById(userInfo.getId_Info()).get();
		 info.setFirstName(userInfo.getFirstName());
		 info.setLastName(userInfo.getLastName());
		 info.setAddress(userInfo.getAddress());
		 info.setBirthday(userInfo.getBirthday());
		 info.setBirthday_type(userInfo.getBirthday_type());
		 info.setSex(userInfo.getSex());
		 
		try {
		 return profileRepository.save(info);
		}catch(Exception e) {
			e.printStackTrace();
			throw new AccessException(e.getMessage());
		}
	}
	public UserInfo getUserInfo(Principal principal) {
		User user = userRepository.findByUsername(principal.getName());
	
		return user.getUserInfo();
	}
	
	
	public String updateUserPhoto(long profileId,MultipartFile file, Crop crop) {
		
		 try {

	        
	            byte[] bytes = file.getBytes();
	            ImageIcon img = new ImageIcon(bytes);
	   
	            float x_ratio = (float) img.getIconWidth() /crop.getDisplayWidth();
	            float y_ratio = (float) img.getIconHeight()/crop.getDisplayHeight();
	            String image_url="";
	            crop.setRatio(x_ratio, y_ratio);
				BufferedImage b = ImageUtils.crop(img.getImage(),crop.getX(),crop.getY(), crop.getWith(),crop.getHeight()	);
			    BufferedImage b2 = ImageUtils.resize(b, crop.getWith(), crop.getHeight(), 200,200);
<<<<<<< HEAD:src/main/java/io/khaminfo/askmore/services/ProfileService.java
			     String imageName = ImageUtils.getRandomName();
			 
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write( b2, "png", baos );
				baos.flush();
				File googleFile = CreateGoogleFile.createGoogleFile("1k9WhJDC3dnrodvn1PynO8a-PatoiJydY", "image/png", imageName+".png",baos.toByteArray());
	            image_url = googleFile.getWebContentLink();
	            b2 = ImageUtils.resize(b, crop.getWith(), crop.getHeight(), 56,56);
	            baos = new ByteArrayOutputStream();
	            ImageIO.write( b2, "png", baos );
				baos.flush();
				googleFile = CreateGoogleFile.createGoogleFile("1k9WhJDC3dnrodvn1PynO8a-PatoiJydY", "image/png", imageName+".png",baos.toByteArray());
	            image_url = googleFile.getWebContentLink()+" "+image_url;
				profileRepository.updateProfilePicture(profileId, image_url);
				
	            return image_url;
=======
			    String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
			   String imageName = ImageUtils.getRandomName();
			    f = new File(f.getAbsolutePath()+"/"+imageName+"."+extension);
				ImageIO.write(b2,extension, f );
				 b2 = ImageUtils.resize(b, crop.getWith(), crop.getHeight(), 56,56);
				f = new File(f.getAbsolutePath().replace("."+extension, ".min."+extension));
				ImageIO.write(b2,extension, f );
	     
	            profileRepository.updateProfilePicture(profileId,imageName);
	            return imageName;
>>>>>>> f9663bf310a8a6fb0af371ea868f2e0ba5c781af:src/main/java/io/khaminfo/ppmtool/services/ProfileService.java

	        } catch (Exception e) {
	        	throw new AccessException("SomeThing went Wrong!");
	        }
		
	}
	
	

}
