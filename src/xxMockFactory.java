public class xxMockFactory implements IFactory {
    public IMemberPostsThread createMemberPostsThread(int type, String info, String uniqueId, String[] msg, ISquare square,
    IUtility utility) {
        return new xxMockMemberPostsThread();
    }
}
