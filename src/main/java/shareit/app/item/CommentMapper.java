package shareit.app.item;

public class CommentMapper {

    public static Comment commentDtoToComment(CommentDto commentDto) {
        return new Comment(commentDto.getId(), commentDto.getText(), commentDto.getItem(),
            commentDto.getAuthor(), commentDto.getCreated());
    }

    public static CommentDtoToReturn commentToCommentDtoToReturn(Comment comment) {
        return new CommentDtoToReturn(comment.getId(), comment.getText(),
            comment.getAuthor().getName(), comment.getCreated());
    }

}
