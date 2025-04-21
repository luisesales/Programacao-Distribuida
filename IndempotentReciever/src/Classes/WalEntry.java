public class WalEntry {
    private String requestId;
    private String payload;
    private RequestStatus status;

    public WalEntry(String requestId, String payload) {
        this.requestId = requestId;
        this.payload = payload;
        this.status = RequestStatus.PENDING;
    }

    public void setStatus(RequestStatus status){
        this.status = status;
    }

    public String getStatus(){
        return status.getLabel();
    }

    public String getId(){
        return requestId;
    }

    public String getPayload(){
        return payload;
    }

    public String getWalEntry(){
        return requestId+" - "+payload+" - "+status.getLabel();
    }
   
}
