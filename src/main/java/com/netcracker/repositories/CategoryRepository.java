package com.netcracker.repositories;

import com.netcracker.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query(value = "with recursive temp1 (\"id\",\"name\",\"description\",\"parent_id\") as(" +
            "select t1.id,t1.name,t1.description,t1.parent_id \n" +
            "from category t1 where t1.id= :id \n" +
            "union all \n" +
            "select t2.id,t2.name,t2.description,t2.parent_id \n" +
            "from category t2 inner join temp1 on (temp1.id=t2.parent_id))"
            + " select * from temp1 order by temp1.id;", nativeQuery = true)
    List<Category> findAllByParentCategory(@Param("id") Long id);

    @Query(value = "with recursive temp1 (\"id\",\"name\",\"description\",\"parent_id\",level) as(\n" +
            "\t\n" +
            "\tselect t1.id,t1.name,t1.description,t1.parent_id,1\n" +
            "\t\tfrom category t1 where t1.id=:id\n" +
            "\t\n" +
            "\tunion all\n" +
            "\t\n" +
            "\tselect t2.id,t2.name,t2.description,t2.parent_id,level+1\n" +
            "\tfrom category t2 inner join temp1 on (temp1.id=t2.parent_id) \n" +
            ")\n" +
            "select t1.id,t1.name,t1.description,t1.parent_id from temp1 t1 where level=2 order by t1.id;", nativeQuery = true)
    List<Category> findAllChilds(@Param("id") Long id);

    Category findByName(String name);
}
