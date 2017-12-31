package mips.datapath.and.control.unit.simulator;

import javax.swing.JTextArea;

public class Datapath {
    String instruction;
    private int i2521, i2016,i1511,i250, i106, i150 ;
    String i50;
    private int readData1, readData2;
    private int currentPCAddress;
    private Adder pcAdder = new Adder();
    private Adder branchAdder = new Adder();
    private Mux regDstMux = new Mux();
    private Mux aluSrcMux = new Mux();
    private Mux memtoRegMux = new Mux();
    private Mux pcSrcMux = new Mux();
    private Mux jumpMux = new Mux();
    private Control control = new Control();
    private SignExtend signExtends = new SignExtend();
    private ALU alu = new ALU();
    private int andGateResult;
    private int jumpAddress;
    private int clockCycles;
    private int jrCntrlUnit;
        
    public Datapath() { 
        RegisterFile.initializeRegisters();
        currentPCAddress = ProgramCounter.getAddress();
    }
    
    public void run() {
        executeInstructions();
    }
    
    public void InstructionSeprator ()
    {
        i2521 =Integer.parseInt( instruction.substring(31-25, 32-21),2);
        i2016 =Integer.parseInt( instruction.substring(31-20, 32-16),2);

        i1511 =Integer.parseInt( instruction.substring(31-15, 32-11),2);
        i250 =Integer.parseInt( instruction.substring(31-25, 32-0),2);
        i106 = Integer.parseInt(instruction.substring(31-10, 32-6), 2);
        i50 = instruction.substring(31-5, 32-0);
        if(instruction.charAt(16) == '1') {
            //System.out.println(instruction.substring(31-15, 32));
            i150 = Integer.parseUnsignedInt(instruction.substring(31-15, 32-0), 2);
            i150 = i150 - (int)Math.pow(2, 16);
        } else {
            i150 = Integer.parseInt(instruction.substring(31-15, 32-0), 2);            
        }
        

    }
    
    public void executeInstructions() {
        
        int initPCValue = currentPCAddress;
        while(ProgramCounter.getAddress() < (Memory.getInstructionMemLength() *4) + initPCValue){
            Instruction currentInstruction = (Instruction)Memory.getInstruction(currentPCAddress);
            currentInstruction.setControl(control);
            currentInstruction.generateMachineCode();
            setInstruction(currentInstruction.getMachineCode());
            print();
            
            currentPCAddress = ProgramCounter.getAddress();
            clockCycles++;
        }
        System.out.println("Number of cycles are : " + clockCycles);
        System.out.println("Register final values : ");
        RegisterFile.printReg();
        
        System.out.println("Memory final values : ");
        Memory.printMem();
    }
    public void setInstruction (String ins)
    {
        instruction= ins;
        InstructionSeprator();
        excute();
    }
    
    private void excute()
    {
        pcAdder.performOperation(currentPCAddress, 4);
        /*String jumpStr = ToBinary.convertToBinary(pcAdder.getOutput()
                , 32).substring(0, 4) + instruction.substring(31 - 25, 32) + "00";*/
        jumpAddress = Integer.parseInt(ToBinary.convertToBinary(pcAdder.getOutput()
                , 32).substring(0, 4) + instruction.substring(31 - 25, 32) + "00", 2);
        
        signExtends.extendInput(i150);
        
        if(i50.equals("001000")) {
            jrCntrlUnit = 1;
        } else {
            jrCntrlUnit = 0;
        }
        
        regDstMux.selectOutput(i2016, i1511, 31, control.getRegdst());
        
        readData1 = RegisterFile.getRegister(i2521).getData();
        readData2 = RegisterFile.getRegister(i2016).getData();
        
        aluSrcMux.selectOutput(readData2, i150, control.getAlusrc());
        alu.execute(control.getAluop(), i50, readData1, aluSrcMux.getOutput(), i106);
        
        if(control.getMemread() == 1) {
            Memory.load(alu.getOutput());
        }
        if(control.getMemwrite() == 1) {
            Memory.store(alu.getOutput(), readData2);
        }
        
        memtoRegMux.selectOutput(alu.getOutput(), Memory.getReadData(), 
                pcAdder.getOutput(), control.getMemtoreg());
        
        if(control.getRegwrite() == 1) {
            RegisterFile.getRegister(regDstMux.getOutput()).setData(memtoRegMux.getOutput());
        }
        
        branchAdder.performOperation(pcAdder.getOutput(), i150 * 4);
        if(control.getBranch() == 1 && alu.getZeroSignal() == 1) {
            andGateResult = 1;
        } else {
            andGateResult = 0;
        }
        
        pcSrcMux.selectOutput(pcAdder.getOutput(), branchAdder.getOutput(),
                andGateResult);
        jumpMux.selectOutput(pcSrcMux.getOutput(), jumpAddress, readData1, control.getJump());
        
        ProgramCounter.setAddress(jumpMux.getOutput());

    }
    
    public void print() {
        System.out.print("Machine code : " + instruction+"\n");
        System.out.print("PC output : " + currentPCAddress+"\n");
        System.out.print("PC + 4 Adder output " + pcAdder.getOutput() +"\n");
        System.out.print("Wires input for Jr Control Block : " + i50+"\n");
        System.out.print("Wire output from JR Control Block : " + jrCntrlUnit+"\n");
        System.out.print("Wires to control unit(Op code wire) : " + instruction.substring(0, 6)+"\n");
        System.out.print("Rs field of instruction : " + i2521+"\n");
        System.out.print("Rt field of instruction : " + i2016+"\n");
        System.out.print("Rd field of instruction : " + i1511+"\n");
        System.out.print("instruction 5 - 0 wire : " + i50+"\n");
        System.out.print("RegDst mux output : " + regDstMux.getOutput()+"\n");
        System.out.print("Offset field of the instruction : " + i150+"\n");
        System.out.print("Sign extend output wire : " + i150 + "(" + signExtends.getOutput() + ")" + "\n");
        System.out.print("Shift-2 output : " + i150 * 4 + "\n");
        System.out.print("Target address adder output : " + branchAdder.getOutput() + "\n");
        System.out.print("read data 1 output wire : " + readData1+"\n");
        System.out.print("read data 2 output wire : " + readData2+"\n");
        System.out.print("alusrc mux output : " + aluSrcMux.getOutput()+"\n");
        System.out.print("alu output : " + alu.getOutput()+"\n");
        System.out.print("alu zero signal : " + alu.getZeroSignal()+"\n");
        System.out.print("wire going to write data in memory : " +readData2+"\n");
        System.out.print("Data memory output : " + Memory.getReadData()+"\n");
        System.out.print("memtoReg mux output wire : " + memtoRegMux.getOutput()+"\n");
        System.out.print("second src for jump mux : " + jumpAddress + "\n");
        System.out.print("pcsrc mux output wire : " + pcSrcMux.getOutput()+"\n");
        System.out.print("pc input : " + jumpMux.getOutput() + "\n");
        System.out.print("and gate result : " + andGateResult + "\n");
        System.out.print("jr flag output : " + jrCntrlUnit + "\n");
        System.out.print("Controls:-\n");
        System.out.print("RegDst control signal : " + control.getRegdst() + "\n");
        System.out.print("Branch control signal : " + control .getBranch() + "\n");
        System.out.print("Jump control signal : " + control.getJump() + "\n");
        System.out.print("MemRead control signal : " + control.getMemread() + "\n");
        System.out.print("MemtoReg control signal : " + control.getMemtoreg() + "\n");
        System.out.print("ALUOp control signal : " + control.getAluop() + "\n");
        System.out.print("MemWrite control signal : " + control.getMemwrite() + "\n");
        System.out.print("ALUSrc control signal : " + control.getAlusrc() + "\n");
        System.out.print("RegWrite control signal : " + control.getRegwrite() + "\n");
        System.out.println("ALU control output : " + alu.getAluCtrl());
        System.out.print("======================="+"\n");
    }
         
    public int getClockCycles() {
        return clockCycles;
    }
    
}
