import org.junit.*;
import java.util.List;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {
    
    @Before
    public void setup() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void createAndRetrieveUser() {
        new User("ma@ma.com", "123456", "Willy").save();        
        User willy = User.find("byEmail", "ma@ma.com").first();
        
        assertNotNull(willy);
        assertEquals("Willy", willy.fullname);        
    }
    
    @Test
    public void tryConnectAsUser() {
        new User("ma@ma.com", "123456", "Willy").save();   
        
        assertNotNull(User.connect("ma@ma.com", "123456"));
        assertNull(User.connect("ma@ma.com", "badpassword"));
        assertNull(User.connect("ma@maaaa.com", "123456"));        
    }
    
    @Test
    public void createPost() {
        User willy = new User("ma@ma.com", "123456", "Willy").save();
        new Post(willy, "mypost","hello world").save();
        
        assertEquals(1, Post.count());
        
        List<Post> willyPost = Post.find("byAuthor", willy).fetch();
        
        assertEquals(1, willyPost.size());
        Post firstPost = willyPost.get(0);
        assertNotNull(firstPost);
        assertEquals(willy, firstPost.author);
        assertEquals("mypost", firstPost.title);
        assertEquals("hello world", firstPost.content);
        assertNotNull(firstPost.postedAt);
    }
    
    @Test
    public void postComments() {
        User willy = new User("ma@ma.com", "123456", "Willy").save();
        Post willyPost = new Post(willy, "mypost","hello world").save();
        
        new Comment(willyPost, "Carlos", "Nice Post").save();
        new Comment(willyPost, "Juan", "Nice Post").save();
        
        List<Comment> willyPostComment = Comment.find("byPost", willyPost).fetch();
        
        assertEquals(2, willyPostComment.size());
        
        Comment firstComment = willyPostComment.get(0);
        assertNotNull(firstComment);
        assertEquals("Carlos", firstComment.author);
        assertEquals("Nice Post", firstComment.content);
        assertNotNull(firstComment.postedAt);
        
        Comment secondComment = willyPostComment.get(1);
        assertNotNull(secondComment);
        assertEquals("Juan", secondComment.author);
        assertEquals("Nice Post", secondComment.content);
        assertNotNull(secondComment.postedAt);
        
    }
    
    @Test
    public void useTheCommentsRelation() {
        User willy = new User("ma@ma.com", "123456", "Willy").save();
        Post willyPost = new Post(willy, "mypost","hello world").save();
        
        willyPost.addComment("Carlos", "Nice Post");
        willyPost.addComment("Juan", "Nice Post");
        
        assertEquals(1, User.count());
        assertEquals(1, Post.count());
        assertEquals(2, Comment.count());
        
        willyPost = Post.find("byAuthor", willy).first();
        assertNotNull(willyPost);
        
        assertEquals(2, willyPost.comments.size());
        assertEquals("Carlos", willyPost.comments.get(0).author);
        
        willyPost.delete();
        
        assertEquals(1, User.count());
        assertEquals(0, Post.count());
        assertEquals(0, Comment.count());        
    }
    
    @Test
    public void fullTest() {
        Fixtures.loadModels("data.yml");
        
        assertEquals(2, User.count());
        assertEquals(3, Post.count());
        assertEquals(3, Comment.count());
        
        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNotNull(User.connect("jeff@gmail.com", "secret"));
        assertNull(User.connect("jeff@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
          
        List<Post> willyPosts = Post.find("author.email", "bob@gmail.com").fetch();
        assertEquals(2, willyPosts.size());
        
        List <Comment> willyComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
        assertEquals(3, willyComments.size());
        
        Post frontPost = Post.find("order by postedAt desc").first();
        assertNotNull(frontPost);
        
        assertEquals("About the model layer", frontPost.title);
        
        assertEquals(2, frontPost.comments.size());
        
        frontPost.addComment("Jim", "Hello guys");
        assertEquals(3, frontPost.comments.size());
        assertEquals(4, Comment.count());        
    }

}