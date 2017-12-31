package mips.datapath.and.control.unit.simulator;

public class IFormat extends Instruction{
    private String operation;
    private int rs;
    private int rt;
    private int offset;
    private String strOffset;  
    private int address;
    private boolean flagBeq;
    
    
    public IFormat(String label, int address, String operation, String rsName,
            String rtName , int offset) {
        super(label, address);
        this.operation = operation;
        this.offset = offset;
        rs = RegisterFile.getRegisterNum(rsName);
        rt = RegisterFile.getRegisterNum(rtName);
        flagBeq = false;
    }
    
    public IFormat(String label, int address, String operation, String rsName,
            String rtName , String targetLabel) {
        super(label, address);
        this.operation = operation;
        rs = RegisterFile.getRegisterNum(rsName);
        rt = RegisterFile.getRegisterNum(rtName);
        strOffset = targetLabel;
        flagBeq = true;
    }

    @Override
    public void setControl(Control control) {
        switch(operation) {
            case "lw" :
                control.setSignals(0, 0, 1, 1, 0, 0, 1, 1, 0);
                break;
            case "sw" :
                control.setSignals(0, 0, 0, 0, 0, 1, 1, 0, 0);
                break;
            case "addi" :
                control.setSignals(0, 0, 0, 0, 0, 0, 1, 1, 0);
                break;
            case "ori" :
                control.setSignals(0, 0, 0, 0, 4, 0, 1, 1, 0);
                break;
            case "beq" :
                control.setSignals(0, 1, 0, 0, 1, 0, 0, 0, 0);
                break;
            case "andi" :
                control.setSignals(0, 0, 0, 0, 3, 0, 1, 1, 0);
                break;
        }
    }
    
    public void generateMachineCode() {
        String machineCode = "";
        switch(operation) {
            case "lw" :
                machineCode += "100011";
                break;
            case "sw" :
                machineCode += "101011";
                break;
            case "addi" :
                machineCode += "001000";
                break;
            case "andi" :
                machineCode += "001100";
                break;
            case "ori" :
                machineCode += "001101";
                break;
            case "beq" :
                machineCode += "000100";
                break;
        }
        
        machineCode += ToBinary.convertToBinary(rs, 5);
        machineCode += ToBinary.convertToBinary(rt, 5);
        if(flagBeq) {
            offset = Memory.getInstructionAddress(strOffset);
            offset = (offset - (ProgramCounter.getAddress() + 4)) / 4;
        }
        
        machineCode += ToBinary.convertToBinary(offset, 16);

        setMachineCode(machineCode);
    }
    
    
}
