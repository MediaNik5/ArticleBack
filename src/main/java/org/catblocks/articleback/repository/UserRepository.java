package org.catblocks.articleback.repository;

import org.catblocks.articleback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>{
}
