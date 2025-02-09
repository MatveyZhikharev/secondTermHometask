package org.example.repository;

import org.example.entity.University;

import java.util.List;

public interface UniversityRepository {
  long generateId();

  List<University> findAll();

  University findById(long universityId);

  void create(University university);

  void update(University university);

  void delete(long universityId);
}
