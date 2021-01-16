public class ClientCmdTest {
    public static void main(String[] args) {
        String hostname = "192.168.1.153";
        int port = 44123;
        Client client = new Client(hostname, port, "a7075b5b-b91d-4448-a0f9-d9b0bec1a726");
        
        String text = "join%%%joe blow%%%MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAraUrQR32fKBlvFbwKM3yq78M7dZYxz84hLMVybZcd3avU6IgnxO8w5A7N8eaCag24wcxsOzccZxObhvzkhmJ5YAx2Xb6mLQNr5I3ny2G/5E2zUA4NmoPIz2vNGlleSrWh7mFT3PIQcwPr977NJelnbp+Q4cgIPNnYs2EFYbc4E1zdv3v60Aik/fWT0/ST+jRD6lw3/usvlLEs2KmRIZKKSUhwDXInwCDC9LRmPnGSrDgiPCYTGtbqS8NM9PHH2KRrtRs7qACiNP4BcKRSyce8A/H/HaQ8BMZTdE1IpBkvHqajsJCPJJDb+MdbbU2+yxmpUhxbauFj0bM7bSvqHvEnQIDAQAB%%%207.244.84.59%%%44123";

        String time = client.sendMessage(text, false);

        LogIt.logInfo(time);
    }
}
