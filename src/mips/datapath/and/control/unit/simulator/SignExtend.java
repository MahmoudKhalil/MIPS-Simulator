package mips.datapath.and.control.unit.simulator;

public class SignExtend {
    private static String extendedOutputBits;
    
    public SignExtend() {
    }
    
    public void extendInput(int inputOffset) {
        extendedOutputBits = Integer.toBinaryString(inputOffset);
        String str = "";
        if(inputOffset < 0)
            str = "1";
        else 
            str = "0";
        
        while(extendedOutputBits.length() < 32) {
            extendedOutputBits = str + extendedOutputBits;
        }
    }
    
    public String getOutput() {
        return extendedOutputBits;
    }
}
