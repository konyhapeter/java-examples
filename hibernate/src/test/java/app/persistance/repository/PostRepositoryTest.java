package app.persistance.repository;

import app.persistance.entity.Comment;
import app.persistance.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("Should save and load a Post with Comments")
    public void saveAndGet() {
        // given
        Post post = new Post();
        post.setAuthor("Alice");
        post.setTitle("Hello World");
        post.setContent("This is my first post!");

        Comment comment1 = new Comment();
        comment1.setAuthor("Alice");
        comment1.setContent("Great post!");
        comment1.setPost(post);

        Comment comment2 = new Comment();
        comment2.setAuthor("Bob");
        comment2.setContent("I agree with Alice.");
        comment2.setPost(post);

        post.getComments().add(comment1);
        post.getComments().add(comment2);


        postRepository.save(post);

        // when
        Post postFromDB = postRepository.findByTitle("Hello World");

        // then
        assertThat(postFromDB.getId()).isNotNull();
        assertThat(postFromDB.getComments().size()).isEqualTo(2);
        assertThat(postFromDB.getComments().getFirst().getId()).isNotNull();
        assertThat(postFromDB.getAuthor()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Should find Posts by author")
    void findByAuthor() {
        // given
        List<String> titles = List.of("Title 1", "Title 2");


        Post post1 = new Post();
        post1.setAuthor("Jane Doe");
        post1.setTitle(titles.getFirst());
        post1.setContent("Content 1");

        Post post2 = new Post();
        post2.setAuthor("Jane Doe");
        post2.setTitle(titles.getLast());
        post2.setContent("Content 2");

        postRepository.saveAll(List.of(post1, post2));

        // when
        List<Post> posts = postRepository.findByAuthor("Jane Doe");

        // then
        titles = posts.stream()
                .map(Post::getTitle)
                .toList();

        assertThat(posts.size()).isEqualTo(2);
        List<String> finalTitles = titles;
        titles.forEach(title -> assertThat(finalTitles.contains(title)));

    }

    @Test
    @DisplayName("Should cascade delete comments when Post is deleted")
    void cascadeDeleteComments() {
        // given
        Post post = new Post();
        post.setAuthor("John Doe");
        post.setTitle("Cascade Test");
        post.setContent("Cascade content");

        Comment comment = new Comment();
        comment.setAuthor("Alice");
        comment.setContent("Cascade comment");
        comment.setPost(post);

        post.getComments().add(comment);
        Post saved = postRepository.save(post);

        Long commentId = saved.getComments().getFirst().getId();

        // when
        postRepository.delete(saved);

        // then
        // Post is deleted, and due to orphanRemoval = true, the Comment should also be deleted
        assertThat(postRepository.findById(saved.getId())).isEmpty();
        assertThat(commentRepository.findAll().size()).isEqualTo(0);
    }


    @Test
    @DisplayName("Should fail when saving Comment with null Post (foreign key violation)")
    void saveCommentWithoutPostFails() {
        // given
        Comment comment = new Comment();
        comment.setAuthor("Alice");
        comment.setContent("This comment has no post!");
        comment.setPost(null); // <-- violates foreign key

        // when / then
        assertThatThrownBy(() -> commentRepository.saveAndFlush(comment))
                .isInstanceOfAny(
                        org.springframework.dao.DataIntegrityViolationException.class
                );
    }
}
