package resource_monitor;

import java.io.File;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.DiskUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarNotImplementedException;

public class ResourceMonitor extends Thread {

	private boolean running;
	private int serverPort;
	private static Sigar sigar;

	public ResourceMonitor(int serverPort) {
		running = false;
		this.serverPort = serverPort;
		sigar = new Sigar();
	}

	public void run() {
		running = true;
		while (running) {
			if (!sendData(serverPort, takeMeasurement())) {
				// Sending message failed
				running = false;
			}

			// End of cycle
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Measurement takeMeasurement() {
		Mem mem = null;
		CpuPerc[] cpu = null;

		try {
			mem = sigar.getMem();
			cpu = sigar.getCpuPercList();
		} catch (SigarException se) {
			se.printStackTrace();
		}
		/*
		 * // Print the measurement for(int i = 0; i<cpu.length; i++) {
		 * System.out.println("CPU core " + i + ":" + cpu[i].getCombined()*100 +
		 * "%"); }
		 * 
		 * System.out.println("Actual total free system memory: " +
		 * mem.getActualFree() / 1024 / 1024+ " MB");
		 * System.out.println("Actual total used system memory: " +
		 * mem.getActualUsed() / 1024 / 1024 + " MB");
		 * System.out.println("Total free system memory ......: " +
		 * mem.getFree() / 1024 / 1024+ " MB");
		 * System.out.println("System Random Access Memory....: " + mem.getRam()
		 * + " MB"); System.out.println("Total system memory............: " +
		 * mem.getTotal() / 1024 / 1024+ " MB");
		 * System.out.println("Total used system memory.......: " +
		 * mem.getUsed() / 1024 / 1024+ " MB");
		 * 
		 * System.out.println("\n**************************************\n");
		 */
		try {
			// CpuInfoList contains information about the clock speed, cache
			// size, model, number of cores of the CPU
			CpuInfo[] cpus = sigar.getCpuInfoList();
			Cpu[] CpuInfo = sigar.getCpuList();
			File[] roots = File.listRoots();
			double[] loadAvg = sigar.getLoadAverage();

			for (File root : roots) {
				System.out.println(root.getAbsolutePath());
				System.out.println(root.getTotalSpace());

			}

			double uptime = sigar.getUptime().getUptime();

			String loadAverage;

			try {
				double[] avg = sigar.getLoadAverage();
				loadAvg[0] = new Double(avg[0]);
				loadAvg[1] = new Double(avg[1]);
				loadAvg[2] = new Double(avg[2]);
				loadAverage = String.format("load average(1 min): %f\nload average(5 min): %f\nload average:(15 min): %f", loadAvg[0], loadAvg[1], loadAvg[2]);

			} catch (SigarNotImplementedException e) {
				loadAverage = "(load average unknown)";
			}

			System.out.println(loadAverage);
			
			System.out.println(sigar.getNetStat().toString());

			DiskUsage disk = sigar.getDiskUsage("/");
			System.out.println(disk);
			for (int i = 0; i < cpus.length; i++) {
				System.out.println(cpus[i].toString());

				System.out.println("\n" + CpuInfo[i].toString());
			}
		} catch (SigarException e) {
			System.out.println("sigar exception");
			e.printStackTrace();
		}

		return new Measurement(mem.getRam(), mem.getTotal(), mem.getTotal()
				- mem.getUsed());
	}

	public boolean sendData(int port, Measurement m) {
		try {
			Socket s = new Socket("localhost", port);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

			oos.writeObject(m);
			System.out.println("Sent message to inbox");
			return true;

		} catch (Exception e) {
			System.out.println("Is there a server running on that port?");
			return false;
		}
	}
}
