package io.khaminfo.ppmtool.web;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.khaminfo.ppmtool.domain.Crop;
import io.khaminfo.ppmtool.domain.Student;
import io.khaminfo.ppmtool.domain.Teacher;
import io.khaminfo.ppmtool.domain.User;
import io.khaminfo.ppmtool.payload.JWTLoginSuccessResponse;
import io.khaminfo.ppmtool.payload.LoginRequest;
import io.khaminfo.ppmtool.security.JWTTokenProvider;
import io.khaminfo.ppmtool.security.SecurityConstants;
import io.khaminfo.ppmtool.services.MapValidationErrorService;
import io.khaminfo.ppmtool.services.UserService;
import io.khaminfo.ppmtool.validator.UserValidator;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private MapValidationErrorService mapErrorService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserValidator validator;
	@Autowired
	private JWTTokenProvider jwtTokenProvider;
	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
		ResponseEntity<?> errorMap = mapErrorService.MapValidationService(result);
		if (errorMap != null)
			return errorMap;
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()

				));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JWTLoginSuccessResponse(true, jwt));
	}

	@PostMapping("/register/{type}")
	public ResponseEntity<?> registerUser(@Valid @RequestBody String req, @PathVariable int type, BindingResult result)
			throws JsonParseException, JsonMappingException, IOException {
		// validator.validate(user, result);
		
		User user = null;
		ObjectMapper mapper = new ObjectMapper();

		if (type == 2)
			user = mapper.readValue(req, Student.class);
		else
			user = mapper.readValue(req, Teacher.class);

		validator.validate(user, result);
		ResponseEntity<?> mappErr = mapErrorService.MapValidationService(result);
		if (mappErr != null)
			return mappErr;
      
		return new ResponseEntity<User>(userService.saveUser(user, type), HttpStatus.CREATED);
	}

	@PostMapping("/confirm/{id}/{code}")
	public ResponseEntity<?> registerUser(@PathVariable long id, @PathVariable String code) {
		System.out.println("id  "+id +"  "+code);
		int result = userService.confirmUser(id, code);
		return new ResponseEntity<String>(result == 1 ? HttpStatus.OK : HttpStatus.NOT_MODIFIED);
	}
	

	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser( Principal principal) {
		userService.logoutUser(principal);	
		return new ResponseEntity<>( HttpStatus.OK);
	}
	
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllUsers() {
		return new ResponseEntity<Iterable<User>>( userService.getAllUsers(),HttpStatus.OK);
	}
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable long id ) {
		return new ResponseEntity<User>( userService.getUserById(id),HttpStatus.OK);
	}

	@GetMapping("/pdfs/{fileName}")
	public void getContent(@PathVariable String  fileName ,  HttpServletResponse response) throws IOException {
		System.out.println("ok "+fileName);
		 Path path = Paths.get("src/main/resources/static"+"/pdfs/" + fileName);
		 
		 DataInputStream in = new DataInputStream(new FileInputStream(path.toFile().getAbsolutePath()));
		System.out.println(in);
		
		response.setHeader("Content-disposition: ", "attachment; filename="+fileName);
		response.setContentType("application/pdf");
		response.setHeader("Content-Transfer-Encoding", "download");
		
		DataOutputStream output = new DataOutputStream(response.getOutputStream());
		long reset = path.toFile().length();
		int buffer_size = 5*1024*1024;
		byte[] buffer;
		while( reset > 0 ){
			if( buffer_size < reset ){
			}else{
				buffer_size = (int)reset;
			}
			reset -= buffer_size;
			buffer = new byte[buffer_size];
			in.readFully(buffer);
			output.write(buffer);
		}
		
	}




}
