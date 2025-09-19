package app.persistance.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Post")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

//    orphanRemoval = true
//
//    This ensures that if a child entity is removed from the parent’s collection, it will be deleted from the database.

//    cascade = CascadeType.ALL
//
//    This tells JPA that certain operations on the parent should be automatically applied to the children.
//    Since we used CascadeType.ALL, it covers all types:
//
//    PERSIST → saving a Post will also save its Comments.
//
//    MERGE → updating a Post will update its Comments.
//
//            REMOVE → deleting a Post will delete its Comments.
//
//            REFRESH → refreshing a Post will refresh its Comments.
//
//            DETACH → detaching a Post will detach its Comments from the persistence context.
//
//    Without cascade, if you tried postRepository.save(post), you’d have to manually save each comment too. With cascade, they’re saved automatically.
}
