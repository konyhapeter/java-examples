package app.persistance.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "Comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, length = 4000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

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