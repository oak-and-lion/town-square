public class SquareFactory {
    private SquareFactory(){}
    
    public static ISquareController create(Utility utility, SampleController sampleController) {
        return new SquareController(utility, sampleController);
    }
}
