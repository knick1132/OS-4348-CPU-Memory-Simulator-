/*
Nicholas Kiv
OS 4348
ozbirn

a program the simulates a CPU fetch cycle using 2 process acting as memory
and the cpu
*/

import java.io.*;
import java.util.*;

import java.lang.Runtime;

public class Project1 {


    public static void main(String[] args){

        String name = args[0];//gets the argument string for file name and timer
        System.out.println(name);
        int timer = Integer.parseInt(args[1]);

        try {

        Runtime rt = Runtime.getRuntime();//creates the child process
        Process proc = rt.exec("java MEM");
        InputStream MEM_in = proc.getInputStream();
        OutputStream MEM_out = proc.getOutputStream();

        System.out.println("CPU executed");

        CPU cpu = new CPU(MEM_in, MEM_out, name, timer);//creates the cpu
        cpu.Execute();//executes the cpu


        }catch (Exception e){
            System.out.println("MEM not running/CPU error generic");
            e.printStackTrace();
        }

    }

}

class CPU{
    
    private int PC = 0;//registers in the cpu
    private int SP = 999;
    private int IR = 0;
    private int AC = 0;
    private int X = 0;
    private int Y = 0;
    private boolean kernelMode = false;//CPU state
    private Scanner MEM_input;
    private PrintWriter MEM_output;
    private int InterruptNum = 30;

    CPU(InputStream input, OutputStream output,String name, int timer){//intializes the cpu
        this.MEM_input = new Scanner(input);
        this.MEM_output = new PrintWriter(output);
        MEM_output.println(name);
        InterruptNum = timer;
    }

    public void Execute(){

        int interruptCount = 0;

        while (true){

            /*
            if(interruptCount < InterruptNum){
                cycle();
                interruptCount++;
            }else if(InterruptNum == -1){
                cycle();
            }
            if(!kernelMode)
                interruptCount++;
            else if(interruptCount > InterruptNum){
                interruptCount = 0;
                performSystemCall(1000);
                //kernelMode = true;
                //returnSystemCall();
            }
            */

            if(interruptCount < InterruptNum){//main execution cycle
                cycle();
                if(kernelMode == false)
                    interruptCount++;
            } else {
                interruptCount = 0;
                performSystemCall(1000);//interrupt
            }
            
        }

    }

    public void cycle(){ //fetch && insturction 
        fetch();
        instructions(IR);
    }

    public void performSystemCall(int tempPC){// systemcall, saves registers and enters kernel mode
        kernelMode = true;
        write(1999, SP);
        write(1998, PC);
        
        PC = tempPC;
        SP = 1997;

        while (kernelMode){//systemcall cycle for execution
            cycle();
        }
      

    }

    public void returnSystemCall(){//return from syscall, leave kernal mode
        SP = read(1999);
        PC = read(1998);

        kernelMode = false;
    }



    public int pop(){//pop from stack
        return read(SP++);
    }

    public void push(int data){//push to stack
        write(--SP, data);
    }


    public int read(int address){ //request the MEM for data at an address
        //System.out.println(IR);

        try{
            MEM_output.println("r," + address + ",-1," + kernelMode);
            MEM_output.flush();
        }catch(Exception e){//check for memory violation
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
        }

        String slot = MEM_input.nextLine();//read input from mem

        try {
            return Integer.parseInt(slot);
        } catch (Exception e) {
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            System.exit(0);
        }

        return 0;

    }

    public void write(int address, int data){//write to memory

        MEM_output.println("w," + address + "," + data + "," + kernelMode);
        MEM_output.flush();
    }

    public void fetch(){//fetch instructions
        IR = read(PC++);
    }

    public void instructions(int temp){//instruction set
        //System.out.println(temp);
        switch(temp){
            case 1:
                fetch();
                AC = IR;
                break;
            case 2: 

                fetch();
                AC = read(IR);
                break;

            case 3:
                fetch();
                AC = read(read(IR));
                break;

            case 4: 
                fetch();
                AC = read(IR + X);
                break;

            case 5: 
                fetch();
                AC = read(IR + Y);
                break;

            case 6: 
                AC = read(SP + X);
                break;
            case 7:
                fetch();
                write(IR, AC);
                break;
            case 8: 
            
                Random rand = new Random();
                AC = rand.nextInt(100) + 1;
                break;

            case 9:

                fetch();
                if (IR == 1)
                    System.out.print(AC);
                
                else if (IR == 2) 
                    System.out.print((char)AC);
                else{
                    System.out.print("Case 9 port error");
                    System.exit(0);
                }
                
                break;

            case 10:
                AC += X;
                break;

            case 11:
                AC += Y;
                break;

            case 12:
                AC -= X;
                break;

            case 13:
                AC -= Y;
                break;

            case 14:
                X = AC;
                break;
            
            case 15:
                AC = X;
                break;
            
            case 16:
                Y = AC;
                break;
            
            case 17: 
                AC = Y;
                break;
            
            case 18:
                SP = AC;
                break;

            case 19:
                AC = SP;
                break;

            case 20:
                fetch();
                PC = IR;
                break;
            
            case 21:
                fetch();
                if(AC == 0)
                    PC = IR;

                break;

            case 22: 
                fetch();
                if(AC != 0)
                    PC = IR;

                break;
            
            case 23:
                fetch();
                push(PC);
                PC = IR;
                
                break;

            case 24:
                PC = pop();
                break;

            case 25:
                X++;
                
                break;
            
            case 26:
                X--;
                break;
            
            case 27:
                push(AC);
                break;
            
            case 28:
                AC = pop();
                break;
            
            case 29:
                performSystemCall(1500);
                break;

            case 30:
                returnSystemCall();
                break;

            case 50:
                Runtime rt = Runtime.getRuntime();//kills both processes
                try {
                    rt.exec("pkill -f java");
                    System.exit(0);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }


}
