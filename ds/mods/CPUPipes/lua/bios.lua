os = {}
os.pullEvent = yield

function write( sText )
	local w,h = term.getSize()		
	local x,y = term.getCursorPos()
	
	local nLinesPrinted = 0
	local function newLine()
		if y + 1 <= h then
			term.setCursorPos(1, y + 1)
		else
			term.setCursorPos(1, h)
			term.scroll(1)
		end
		x, y = term.getCursorPos()
		nLinesPrinted = nLinesPrinted + 1
	end
	
	-- Print the line with proper word wrapping
	while string.len(sText) > 0 do
		local whitespace = string.match( sText, "^[ \t]+" )
		if whitespace then
			-- Print whitespace
			term.write( whitespace )
			x,y = term.getCursorPos()
			sText = string.sub( sText, string.len(whitespace) + 1 )
		end
		
		local newline = string.match( sText, "^\n" )
		if newline then
			-- Print newlines
			newLine()
			sText = string.sub( sText, 2 )
		end
		
		local text = string.match( sText, "^[^ \t\n]+" )
		if text then
			sText = string.sub( sText, string.len(text) + 1 )
			if string.len(text) > w then
				-- Print a multiline word				
				while string.len( text ) > 0 do
					if x > w then
						newLine()
					end
					term.write( text )
					text = string.sub( text, (w-x) + 2 )
					x,y = term.getCursorPos()
				end
			else
				-- Print a word normally
				if x + string.len(text) - 1 > w then
					newLine()
				end
				term.write( text )
				x,y = term.getCursorPos()
			end
		end
	end
	
	return nLinesPrinted
end

function print( ... )
	local nLinesPrinted = 0
	for n,v in ipairs( { ... } ) do
		nLinesPrinted = nLinesPrinted + write( tostring( v ) )
	end
	nLinesPrinted = nLinesPrinted + write( "\n" )
	return nLinesPrinted
end

function read( _sReplaceChar, _tHistory )
	--term.setCursorBlink( true )

    local sLine = ""
	local nHistoryPos = nil
	local nPos = 0
    if _sReplaceChar then
		_sReplaceChar = string.sub( _sReplaceChar, 1, 1 )
	end
	
	local w, h = term.getSize()
	local sx, sy = term.getCursorPos()	
	
	local function redraw( _sCustomReplaceChar )
		local nScroll = 0
		if sx + nPos >= w then
			nScroll = (sx + nPos) - w
		end
			
		term.setCursorPos( sx, sy )
		local sReplace = _sCustomReplaceChar or _sReplaceChar
		if sReplace then
			term.write( string.rep(sReplace, string.len(sLine) - nScroll) )
		else
			term.write( string.sub( sLine, nScroll + 1 ) )
		end
		term.setCursorPos( sx + nPos - nScroll, sy )
	end
	
	while true do
		local sEvent, param = os.pullEvent()
		if sEvent == "char" then
			sLine = string.sub( sLine, 1, nPos ) .. param .. string.sub( sLine, nPos + 1 )
			nPos = nPos + 1
			redraw()
			
		elseif sEvent == "key" then
		    if param == 28 then
				-- Enter
				break
				
			elseif param == 203 then
				-- Left
				if nPos > 0 then
					nPos = nPos - 1
					redraw()
				end
				
			elseif param == 205 then
				-- Right				
				if nPos < string.len(sLine) then
					nPos = nPos + 1
					redraw()
				end
			
			elseif param == 200 or param == 208 then
                -- Up or down
				if _tHistory then
					redraw(" ");
					if param == 200 then
						-- Up
						if nHistoryPos == nil then
							if #_tHistory > 0 then
								nHistoryPos = #_tHistory
							end
						elseif nHistoryPos > 1 then
							nHistoryPos = nHistoryPos - 1
						end
					else
						-- Down
						if nHistoryPos == #_tHistory then
							nHistoryPos = nil
						elseif nHistoryPos ~= nil then
							nHistoryPos = nHistoryPos + 1
						end						
					end
					
					if nHistoryPos then
                    	sLine = _tHistory[nHistoryPos]
                    	nPos = string.len( sLine ) 
                    else
						sLine = ""
						nPos = 0
					end
					redraw()
                end
			elseif param == 14 then
				-- Backspace
				if nPos > 0 then
					redraw(" ");
					sLine = string.sub( sLine, 1, nPos - 1 ) .. string.sub( sLine, nPos + 1 )
					nPos = nPos - 1					
					redraw()
				end
			elseif param == 199 then
				-- Home
				nPos = 0
				redraw()		
			elseif param == 211 then
				if nPos < string.len(sLine) then
					redraw(" ");
					sLine = string.sub( sLine, 1, nPos ) .. string.sub( sLine, nPos + 2 )				
					redraw()
				end
			elseif param == 207 then
				-- End
				nPos = string.len(sLine)
				redraw()
			end
		end
	end
	
	--term.setCursorBlink( false )
	term.setCursorPos( w + 1, sy )
	print()
	
	return sLine
end

local startupFiles = {"boot","main","run","startup"}
local code
local restart = false
function os.restart()
	restart = true
	yield()
end
repeat
	restart = false
	if fs.isSDCardIn() then
		for i, v in pairs(startupFiles) do
			if fs.exists(v) then
				print("Loading file "..v)
				local fh = fs.open(v,"r")
				code = fh.readAll()
				fh.close()
				break
			end
		end
		if code then
			local f, e = load(code,"boot")
			if not f then
				print("Syntax error")
				print(e)
				return
			end
			local coro = coroutine.create(f)
			local event = {}
			while true do
				if coroutine.status(coro) == "suspended" then
					local s, e = coroutine.resume(coro, unpack(event))
					if not s then
						print(e)
						break
					end
					if restart then break end
					event = {yield()}
				end
			end
		else
			print("Startup file not found!")
		end
	else
		print("Please insert an SDCard")
	end
until not restart
