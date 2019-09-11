
/*
Nicholas Kiv
OS 4348
ozbirn

the memory process of the program
*/

import java.io.*;
import java.util.*;

import java.lang.Runtime;

public class MEM {

    public static int point = 0;
    public static int MEM[] = new int[2000];

  	public static void main(String[] args){


        Scanner fromCPU = new Scanner(System.in);
        String name = fromCPU.nextLine();
        initMEM(name);

        while(true){//waits for cpu to ask for data

            if(fromCPU.hasNextLine()){ //parses the line in the format of "type, address, int, kernalmode"
                String slot = fromCPU.nextLine();//reads lines/slots from the CPU
                char type = slot.charAt(0);
                int address = Integer.parseInt(slot.split(",")[1]);
                int data = Integer.parseInt(slot.split(",")[2]);
                boolean kernelmode = Boolean.parseBoolean(slot.split(",")[3]);

                
                if(address >=1000 && kernelmode == false){//checks if its in kernel mode

                    System.out.println("Memory violation: accessing system address " + address + " in user mode");
                    System.exit(0);
                    
                } else {

                    if(type == 'r')//read
                        System.out.println(read(address));
                
                    else if(type == 'w')// write
                        write(data, address);
                }  
            }     
        }
    }

    public static void initMEM(String name){//reads the file and parses at the same time
        try {
            File file = new File(name);
            Scanner scanner = new Scanner(file);
            String slot;

            while(scanner.hasNextLine()) {
                try{
                    slot = scanner.nextLine().split(" ")[0];

                    if(slot.indexOf(".") != -1){
                            point = Integer.parseInt(slot.substring(1, slot.length()));
                            //System.out.println(point);
                        }
                    else {
                        MEM[point++] = Integer.parseInt(slot);
                    }
                }catch(Exception e){
                      point--;
                }

                
            }

        } catch (IOException e) {
            System.out.print("file error, cannot read/incorrect format");// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static int read(int address){//read from memory
        return MEM[address];
    }
    public static void write(int data, int address){//write to memory
        MEM[address] = data;
    }
}
