package mips.datapath.and.control.unit.simulator;

import java.util.Scanner;

public class MIPSDatapathAndControlUnitSimulator {
    
    public static void main(String[] args) {
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the starting address for the program :-");
        int startingAddr = scan.nextInt();
        ProgramCounter.setAddress(startingAddr);
        Datapath datapath = new Datapath();
        //inputmem();
        //System.out.println("Please enter the number of instructions to be executed :- ");
        //int numOfInstr = scan.nextInt();
        //scan.nextLine();
        //inputInstructions(numOfInstr, startingAddr);
        Memory.store(1000, 1);
        Memory.store(1004, 2);
        Memory.store(1008, 3);
        Memory.store(1012, 4);
        Memory.store(1016, 5);
        Memory.store(1020, 6);
        Memory.store(1024, 7);
        Memory.store(1028, 8);
        Memory.store(1032, 9);
        Memory.store(1036, 10);
        
        /*add $t0, $s0, $zero
        addi $t1, $zero, 9
        sll $t1, $t1, 2
        add $t1, $t1, $s1
        L1:lw $t2, 0($t0)
        sw $t2, 0($t1)
        addi $t0, $t0, 4
        addi $t1, $t1, -4
        slt $t3, $t1, $s1
        beq $t3, $zero, L1
        */
        
        Memory.addInstruction(new IFormat("", startingAddr, "addi", "$0", "$s0", 1000));
        Memory.addInstruction(new IFormat("", startingAddr + 4, "addi", "$0", "$s1", 2000));
        Memory.addInstruction(new RFormat("", startingAddr + 8, "add", "$s0", "$0", "$t0", 0));
        Memory.addInstruction(new IFormat("", startingAddr + 12, "addi", "$0", "$t1", 9));
        Memory.addInstruction(new RFormat("", startingAddr + 16, "sll", "$0", "$t1", "$t1", 2));
        Memory.addInstruction(new RFormat("", startingAddr + 20, "add", "$t1", "$s1", "$t1", 0));
        Memory.addInstruction(new IFormat("L1", startingAddr + 24, "lw", "$t0", "$t2", 0));
        Memory.addInstruction(new IFormat("", startingAddr + 28, "sw", "$t1", "$t2", 0));
        Memory.addInstruction(new IFormat("", startingAddr + 32, "addi", "$t0", "$t0", 4));
        Memory.addInstruction(new IFormat("", startingAddr + 36, "addi", "$t1", "$t1", -4));
        Memory.addInstruction(new RFormat("", startingAddr + 40, "slt", "$t1", "$s1", "$t3", 0));
        Memory.addInstruction(new IFormat("", startingAddr + 44, "beq", "$t3", "$0", "L1"));
        
        datapath.executeInstructions();
        
    }
    public static void inputmem(){
        System.out.println("Enter 1 to add new data in memory or -1 to skip");
        int x; 
        Scanner scan = new Scanner(System.in);
        x = scan.nextInt();
        while(x == 1)
        {
            System.out.println("Enter data address :-");
            int address = scan.nextInt();
            System.out.println("Enter data value :-");
            int data = scan.nextInt();
            Memory.store(address, data);
            System.out.println("Press 1 to add new data in memory or -1 to skip");
            x = scan.nextInt();
        }
    }
    
    public static void printInstr() {
        System.out.println("Please choose one of the operations below :- ");
        System.out.println("(1) for add\t(8) for addi");
        System.out.println("(2) for and\t(9) for andi");
        System.out.println("(3) for or\t(10) for ori");
        System.out.println("(4) for nor\t(11) for lw");
        System.out.println("(5) for sll\t(12) for sw");
        System.out.println("(6) for slt\t(13) for jal");
        System.out.println("(7) for beq\t(14) for j");
        System.out.println("(15) for jr");
    }
    
    public static void printRegChoices() {
        System.out.println("Please choose one of the registers below");
        for(int i = 0, j = 16; i < 16; i++, j++) {
            System.out.println("("+i+")"+ "for " + RegisterFile.getRegister(i).getName()+
                    "\t(" + j+")" + "for " + RegisterFile.getRegister(j).getName());
        }
    }
    
    public static void inputInstructions(int numOfInstr, int startingAddr) {
        Scanner scan = new Scanner(System.in);
        for(int i = 0; i < numOfInstr; i++) {
            System.out.println("Please enter the label of the instruction if it has one or press enter:-");
            String label = scan.nextLine();
            printInstr();
            System.out.print("Choice : ");
            int operation = scan.nextInt();
            switch(operation) {
                case 1:
                    rFormat(label, startingAddr, "add");
                    break;
                case 2:
                    rFormat(label, startingAddr, "and");
                    break;
                case 3:
                    rFormat(label, startingAddr, "or");
                    break;
                case 4:
                    rFormat(label, startingAddr, "nor");
                    break;
                case 5:
                    sll(label, startingAddr);
                    break;
                case 6:
                    rFormat(label, startingAddr, "slt");
                    break;
                case 7:
                    beq(label, startingAddr);
                    break;
                case 8:
                    iFormat(label, startingAddr, "addi");
                    break;
                case 9:
                    iFormat(label, startingAddr, "andi");
                    break;
                case 10:
                    iFormat(label, startingAddr, "ori");
                    break;
                case 11:
                    iFormat(label, startingAddr, "lw");
                    break;
                case 12:
                    iFormat(label, startingAddr, "sw");
                    break;
                case 13:
                    jFormat(label, startingAddr, "jal");
                    break;
                case 14:
                    jFormat(label, startingAddr, "j");
                    break;
                case 15:
                    jr(label, startingAddr);
                    break;
                default:
                    System.out.println("Invalid input!");
                    i--;
                    break;
            }
            startingAddr += 4;
            scan.nextLine();
        }
        
        System.out.println("=============");
    
    }
    
    public static void sll(String label, int address) {
        printRegChoices();
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your destination register : ");
        int choice = scan.nextInt();
        String regDst = RegisterFile.getRegister(choice).getName();
        System.out.print("Enter your target register : ");
        choice = scan.nextInt();
        String regTrgt = RegisterFile.getRegister(choice).getName();
        System.out.print("Enter the shift amount : ");
        int shamt = scan.nextInt();
        Memory.addInstruction(new RFormat(label, address, "sll", "$0", regTrgt, regDst, shamt));
    }
    
    public static void beq(String label, int address) {
        printRegChoices();
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your source register : ");
        int choice = scan.nextInt();
        String regSrc = RegisterFile.getRegister(choice).getName();
        System.out.print("Enter your target register: ");
        choice = scan.nextInt();
        String regTrgt = RegisterFile.getRegister(choice).getName();
        System.out.print("Enter your target instruction : ");
        String targetAddress = scan.next();
        Memory.addInstruction(new IFormat(label, address, "beq", regSrc, regTrgt, targetAddress));
    }
    
    public static void jr(String label, int address) {
        printRegChoices();
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your source register : ");
        int choice = scan.nextInt();
        String regSrc = RegisterFile.getRegister(choice).getName();
        Memory.addInstruction(new RFormat(label, address, "jr", regSrc, "$0", "$0", 0));
    }
    
    public static void rFormat(String label, int address, String op) {
        printRegChoices();
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your destination register : ");
        int choice = scan.nextInt();
        String regDst = RegisterFile.getRegister(choice).getName();
        System.out.print("Enter your source register : ");
        choice = scan.nextInt();
        String regSrc = RegisterFile.getRegister(choice).getName();
        System.out.print("Enter your second source register : ");
        choice = scan.nextInt();
        String regTrgt = RegisterFile.getRegister(choice).getName();
        Memory.addInstruction(new RFormat(label, address, op, regSrc, regTrgt, regDst, 0));
    }
    
    public static void iFormat(String label, int address, String op) {
        printRegChoices();
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your target register : ");
        int choice  = scan.nextInt();
        String regTrgt = RegisterFile.getRegister(choice).getName();
        System.out.print("Enter your source register : ");
        choice = scan.nextInt();
        String regSrc = RegisterFile.getRegister(choice).getName();
        System.out.print("Enter constant value : ");
        int constant = scan.nextInt();
        Memory.addInstruction(new IFormat(label, address, op, regSrc, regTrgt, constant));
    }
    
    public static void jFormat(String label , int address, String op) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your target instruction label : ");
        String trgtAddress = scan.next();
        Memory.addInstruction(new JFormat(label, address, op, trgtAddress));
    }
}