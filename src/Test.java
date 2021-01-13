public class Test {
    public static void main(String[] args) {
        Utility utility = Utility.create();
        Utility utility2 = Utility.forceNew();

        String password = "8JLOCxT612345678";

        String data = "join%%%john law%%%MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApsRCab4SDmN1FjBdy11rlIUJg5GbczLnj4dMrqzdVZ7zSYThihSdRHx1HcU+gejwwP/q28vuoClFIOj4kOr73By3UUBWdZLRhOH7NcpLCpiIRINU0poHIHwkmM6L4PdvmUTP099VCmyNlndI/AesCD/ZzSZJDsIyuY36MMJNnjbsHd26tFBDCKqIDRixWUPOR+FX+539YQqGgU3RIBF9uNc6T0aWqijNIlMbMxAOMiUFWbDt+wqQWY6sBgPDVWBZqvXEg+CwzdlUYB5LGbGjwb/xyMiUcCkef14faYsCLu6lXQaRQbKgcmWseQPllhyGwwoyylFdx9GFhT2i1VYWSwIDAQAB%%%207.244.67.150%%%44423%%%d1de5968-814d-4a40-8693-542008c80e1c";

        String encrypted = utility.encrypt(data, password);
        LogIt.LogInfo(encrypted);
        String decrypted = utility2.decrypt(encrypted, password);

        LogIt.LogInfo(data);
        LogIt.LogInfo(decrypted);
    }
}
