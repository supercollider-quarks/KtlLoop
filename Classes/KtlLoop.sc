/****

/// make an example with a synth, and simple set message,
/// spec/scaling by hand !

// assume all events are parameter set actions,
// provide simple facilities for that.
// the ones that are numerical can be rescaled and shifted

/* Questions:
   push currState to object when changing rescaling?
*/

*****/

KtlLoop : EventLoop {

	classvar <allKtls;

	var <>paramsToRecord, <currState;
	var <>object, <>ktlMap;

	var <>scaler=1, <>shift=0.0,
	<>rescaled=false, <nonRescalableCtls, inverter=1;

	*all { ^allKtls }
	*initClass { allKtls = () }

	isInverse { ^inverter == -1 }
	invert { inverter = -1 }
	up { inverter = 1 }
	flipInv { inverter = inverter.neg }

	dontRescale { arg ... ids; nonRescalableCtls.addAll(ids.flat); }
	doRescale { arg ... ids; nonRescalableCtls.removeAll(ids.flat); }
	resetScaling { scaler = 1; shift = 0; inverter = 1; }

	init {
		super.init;

		paramsToRecord = [];
		currState = ();
		ktlMap = ();
		nonRescalableCtls = [];
	}

	rescaleVal { |val, clip = true|
		var scaled = val - 0.5 * scaler + shift * inverter + 0.5;
		if (clip) { scaled = scaled.clip(0, 1) };
		^scaled
	}

	rescaleEvent { |event|
		event.keysValuesDo { |par, val|
			if (nonRescalableCtls.indexOf(par).isNil) {
				// replace with rescaled value
				val = this.rescaleVal(val);
				event.put(par, val);
			};
		};
		^event
	}

	setuObj {|event|
		var parEvent = event.select({ |val, key|
			paramsToRecord.includes(key);
		});

		if (rescaled) { parEvent = this.rescaleEvent(parEvent) };

		currState.putAll(parEvent);
		parEvent.keysValuesDo { |par, val|
			object.setUni(ktlMap[par], val);
		}
	}

	defaultFunc {
		^{  |ev| if (ev[\type] == \set) { this.setuObj(ev); } };
	}

	postKtlInfo {
		var str =
		(this.asString ++ ".paramsToRecord: (%);\n"
		    .format(paramsToRecord.asCompileString) +
		this.asString ++ ".nonRescalableCtls: (%);\n"
		    .format(nonRescalableCtls.asCompileString) +
		this.asString ++ ".ktlMap: (%);\n")
		    .format(ktlMap.asCompileString);
		str.newTextWindow(this.class.asString ++ "_" ++ this.key ++ "_settings");
		^str
	}
}
