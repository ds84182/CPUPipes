/*******************************************************************************
* Copyright (c) 2008 LuaJ. All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
******************************************************************************/
package ds.mods.CPUPipes.luaj.vm2.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

import ds.mods.CPUPipes.luaj.vm2.Globals;
import ds.mods.CPUPipes.luaj.vm2.LoadState;
import ds.mods.CPUPipes.luaj.vm2.Lua;
import ds.mods.CPUPipes.luaj.vm2.LuaClosure;
import ds.mods.CPUPipes.luaj.vm2.LuaError;
import ds.mods.CPUPipes.luaj.vm2.LuaFunction;
import ds.mods.CPUPipes.luaj.vm2.LuaTable;
import ds.mods.CPUPipes.luaj.vm2.LuaValue;
import ds.mods.CPUPipes.luaj.vm2.Prototype;
import ds.mods.CPUPipes.luaj.vm2.Varargs;
import ds.mods.CPUPipes.luaj.vm2.lib.jse.CoerceJavaToLua;
import ds.mods.CPUPipes.luaj.vm2.lib.jse.JsePlatform;

/**
 * 
 * @author jim_roseborough
 */
public class LuaScriptEngine implements ScriptEngine, Compilable {
    
	private static final String __ENGINE_VERSION__   = Lua._VERSION;
    private static final String __NAME__             = "Luaj";
    private static final String __SHORT_NAME__       = "Luaj";
    private static final String __LANGUAGE__         = "lua";
    private static final String __LANGUAGE_VERSION__ = "5.2";
    private static final String __ARGV__             = "arg";
    private static final String __FILENAME__         = "?";
    
    private static final ScriptEngineFactory myFactory = new LuaScriptEngineFactory();
    
    private ScriptContext defaultContext;

    private final Globals _G;

    public LuaScriptEngine() {

        // create globals
        _G = "true".equals(System.getProperty("luaj.debug"))?
        		JsePlatform.debugGlobals():
        		JsePlatform.standardGlobals();
    	
    	// set up context
    	ScriptContext ctx = new SimpleScriptContext();
    	ctx.setBindings(createBindings(), ScriptContext.ENGINE_SCOPE);
        setContext(ctx);
        
        // set special values
        put(LANGUAGE_VERSION, __LANGUAGE_VERSION__);
        put(LANGUAGE, __LANGUAGE__);
        put(ENGINE, __NAME__);
        put(ENGINE_VERSION, __ENGINE_VERSION__);
        put(ARGV, __ARGV__);
        put(FILENAME, __FILENAME__);
        put(NAME, __SHORT_NAME__);
        put("THREADING", null);

        // Let globals act as an index metatable
        _G.set(LuaValue.INDEX, _G);
    }
    
    public Object eval(String script) throws ScriptException {
        return eval(new StringReader(script));
    }
    
    public Object eval(String script, ScriptContext context) throws ScriptException {
    	return eval(new StringReader(script), context);
    }
    
    public Object eval(String script, Bindings bindings) throws ScriptException {
        return eval(new StringReader(script), bindings);
    }
    
    public Object eval(Reader reader) throws ScriptException {
        return eval(reader, getContext());
    }
    
    public Object eval(Reader reader, ScriptContext scriptContext) throws ScriptException {
    	return compile(reader).eval(scriptContext);
    }
    
    public Object eval(Reader reader, Bindings bindings) throws ScriptException {
    	ScriptContext c = getContext();
        Bindings current = c.getBindings(ScriptContext.ENGINE_SCOPE);
        c.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        Object result = eval(reader);
        c.setBindings(current, ScriptContext.ENGINE_SCOPE);
        return result;
    }
    
    public void put(String key, Object value) {
		Bindings b = getBindings(ScriptContext.ENGINE_SCOPE);
		b.put(key, value);
    }
    
    public Object get(String key) {
		Bindings b = getBindings(ScriptContext.ENGINE_SCOPE);
		return b.get(key);
    }

    public Bindings getBindings(int scope) {
        return getContext().getBindings(scope);
    }
    
    public void setBindings(Bindings bindings, int scope) {
        getContext().setBindings(bindings, scope);
    }
    
    public Bindings createBindings() {
        return new SimpleBindings();
    }
    
    public ScriptContext getContext() {
        return defaultContext;
    }
    
    public void setContext(ScriptContext context) {
        defaultContext = context;
    }
    
    public ScriptEngineFactory getFactory() {
        return myFactory;
    }

	public CompiledScript compile(String script) throws ScriptException {
		return compile(new StringReader(script));
	}
	
	public CompiledScript compile(Reader reader) throws ScriptException {
		try {
	    	InputStream ris = new Utf8Encoder(reader);
	    	try {
	    		final LuaFunction f = LoadState.load(ris, "script", "bt", _G);
	    		if ( f.isclosure() ) {
	    			// most compiled functions are closures with prototypes 
	    			final Prototype p = f.checkclosure().p;
					return new CompiledScriptImpl() {
						protected LuaFunction newFunctionInstance(LuaTable env) {
							return new LuaClosure( p, env );
						}
					};
	    		} else {
	    			// when luajc is used, functions are java class instances
	    			final Class c = f.getClass();
					return new CompiledScriptImpl() {
						protected LuaFunction newFunctionInstance(LuaTable env) throws ScriptException {
							try {
						        LuaFunction f = (LuaFunction) c.newInstance();
						        f.initupvalue1( env );
						        return f;
							} catch (Exception e) {
								throw new ScriptException("instantiation failed: "+e.toString());
							}
						}
					};	    			
	    		}
			} catch ( LuaError lee ) {
				throw new ScriptException(lee.getMessage() );
			} finally { 
				ris.close();
			}
		} catch ( Exception e ) {
			throw new ScriptException("eval threw "+e.toString());
		}
	}
	
	abstract protected class CompiledScriptImpl extends CompiledScript {
		abstract protected LuaFunction newFunctionInstance(LuaTable env) throws ScriptException; 
		public ScriptEngine getEngine() {
			return LuaScriptEngine.this;
		}
		public Object eval(ScriptContext context) throws ScriptException {
	        Bindings b = context.getBindings(ScriptContext.ENGINE_SCOPE);
	        BindingsGlobals env = new BindingsGlobals(b);
	        LuaFunction f = newFunctionInstance(env);
	        try {
	        	return f.invoke(LuaValue.NONE);
	        } finally {
	        	env.copyout();
	        }
		}
	}

	class BindingsGlobals extends Globals {
		final Bindings b;
		BindingsGlobals(Bindings b) {
			this.b = b;
			this.setmetatable(_G);
			this.debuglib = _G.debuglib;
			for (Entry<String,Object> e : b.entrySet())
				rawset(toLua(e.getKey()), toLua(e.getValue()));
		}
		void copyout() {
			b.clear();
			for (Varargs v = next(LuaValue.NIL); !v.arg1().isnil(); v = next(v.arg1()))
				b.put(toJava(v.arg1()).toString(), toJava(v.arg(2)));
		}
	}

	LuaValue toLua(Object javaValue) {
		return javaValue == null? LuaValue.NIL:
			javaValue instanceof LuaValue? (LuaValue) javaValue:
			CoerceJavaToLua.coerce(javaValue);
	}
	
	Object toJava(LuaValue v) {
		switch ( v.type() ) {
		case LuaValue.TNIL: return null;
		case LuaValue.TSTRING: return v.tojstring();
		case LuaValue.TUSERDATA: return v.checkuserdata(Object.class);
		case LuaValue.TNUMBER: return v.isinttype()? (Object) new Integer(v.toint()): (Object) new Double(v.todouble());
		default: return v;
		}
	}

	// ------ convert char stream to byte stream for lua compiler ----- 

	private final class Utf8Encoder extends InputStream {
		private final Reader r;
		private final int[] buf = new int[2];
		private int n;

		private Utf8Encoder(Reader r) {
			this.r = r;
		}

		public int read() throws IOException {
			if ( n > 0 )
				return buf[--n];
			int c = r.read();
			if ( c < 0x80 )
				return c;
			n = 0;
			if ( c < 0x800 ) {
				buf[n++] = (0x80 | ( c      & 0x3f));				
				return     (0xC0 | ((c>>6)  & 0x1f));
			} else {
				buf[n++] = (0x80 | ( c      & 0x3f));				
				buf[n++] = (0x80 | ((c>>6)  & 0x3f));
				return     (0xE0 | ((c>>12) & 0x0f));
			}
		}
	}
}
