package ds.mods.CPUPipes.server;

import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import ds.mods.CPUPipes.luaj.vm2.LuaThread;
import ds.mods.CPUPipes.luaj.vm2.Varargs;

public class CPUThread extends Thread {

	public LuaThread thread;
	public boolean running = false;
	public boolean waiting = false;
	public boolean terminated = false;
	public int runningFor = 0;
	public String error;
	public ArrayDeque<Varargs> eventQueue = new ArrayDeque<Varargs>();

	public CPUThread(LuaThread t) {
		thread = t;
	}

	@Override
	public void run() {
		synchronized(this)
		{
			try {
				waiting = true;
				wait();
			} catch (InterruptedException ex) {
				Logger.getLogger(CPUThread.class.getName()).log(Level.SEVERE, null, ex);
			}
			waiting = false;
		}
		while (thread.state.status != LuaThread.STATUS_DEAD && !terminated) {
			while (eventQueue.peek() == null)
			{
				synchronized(this)
				{
					try {
						waiting = true;
						wait();
					} catch (InterruptedException ex) {
						Logger.getLogger(CPUThread.class.getName()).log(Level.SEVERE, null, ex);
					}
					waiting = false;
				}
			}
			running = true;
			Varargs event = eventQueue.pop();
			/*else
			{
				event = LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf("tick")});
			}*/
			Varargs v = thread.resume(event);
			running = false;
			runningFor = 0;
			if (!v.toboolean(1))
			{
				error = v.tojstring(2);
			}
		}
	}
}
