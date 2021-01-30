public interface IFactory {
    IMemberPostsThread createMemberPostsThread(int type, String info, String uniqueId, String[] msg, ISquare square,
            IUtility utility);
}
