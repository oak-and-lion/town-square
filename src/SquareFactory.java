public class SquareFactory {
    private SquareFactory(){}
    
    public static ISquareController create(IUtility utility, IDialogController sampleController) {
        return new SquareController(utility, sampleController);
    }
}
