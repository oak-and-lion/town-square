public class ShowSquareMembers implements IShowSquareMembers {
    public void showMembers(ISquare square, IFactory factory, IUtility utility) {
        IModalViewer viewer = factory.createModalViewer(Constants.BASE_MODAL_MEMBER_VIEWER, utility, square);
        viewer.show(square.getSafeLowerName() + Constants.MEMBERS_FILE_EXT);
    }
}
