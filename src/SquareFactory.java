public class SquareFactory {
    private SquareFactory(){}
    
    public static ISquareController create(IUtility utility, ISampleController sampleController) {
        return new SquareController(utility, sampleController);
    }
}
