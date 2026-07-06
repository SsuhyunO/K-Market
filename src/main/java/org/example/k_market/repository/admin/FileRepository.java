package org.example.k_market.repository.admin;

import org.example.k_market.entity.admin.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {

}