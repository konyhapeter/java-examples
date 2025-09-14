package app.persistance.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import app.persistance.entity.Post;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void saveAndGet(){
        // given
        Post post = new Post();
        post.setAuthor("Alice");
        post.setTitle("Hello World");
        post.setContent("This is my first post!");


        postRepository.save(post);

        // when
        Post found = postRepository.findByTitle("Hello World");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getAuthor()).isEqualTo("Alice");
    }
}
