package io.khaminfo.ppmtool.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.khaminfo.ppmtool.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
  User findByUsername(String username);
  @Override
  Iterable<User> findAll() ;
  User getById(Long id);
  User findByIdAndConfirmPassword(long id , String confirmPassword);
  @Modifying
  @Transactional
  @Query("update User u set u.user_state = 4 , u.confirmPassword = :code where u.id = :id")
  int updateUserConirmation(@Param("code") String code, 
    @Param("id") long id);
  @Modifying
  @Transactional
  @Query("update User u set u.password = :password where u.username = :name ")
  int updateUserPassword( @Param("password") String newPassword,    @Param("name") String name);
  @Modifying
  @Transactional
  @Query("update User u set u.user_state = :state where u.id = :id ")
  int updateUserState(  @Param("id") long id , @Param("state") int state);
  
  @Modifying
  @Transactional
  @Query("update User u set u.last_Visit_date = :date where u.username = :name")
  int updateVisitDate(  @Param("date") Date visit_date,@Param("name") String name) ;
}



