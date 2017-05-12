// Using a KtlLoop together with Modality:
// control a sound with an MKtl - a nanoKontrol2,
// record the slider movements with a KtlLoop.


s.boot;

// make an MKtl
(
~mktl = MKtl(\x, "*trol2");
~mkgui = ~mktl.gui;
~mktl.elAt(\sl).action = { |el| el.value.postln };
// move sliders to see that they do something
)

// make a sound to control
(
Ndef(\form, { |freq = 50, shape = 0.9, ffreq = 200, amp = 0.1, lag = 0.1|
    var snd = VarSaw.ar(freq.lag(lag) + [0.3, -0.3], 0, shape.lag(lag));
    Formlet.ar(snd * amp.lag(lag), ffreq.lag(lag), 0.003, 0.03).softclip;
}).play(vol: 0.2);
// add specs for its unusual params:
Ndef(\form).addSpec(\freq, [20, 200]);
Ndef(\form).addSpec(\shape, [0.5, 1]);
Ndef(\form).addSpec(\ffreq, [200, 3000, \exp]);
Ndef(\form).gui;
)

(
// map the first 4 sliders to the 4 sound params
~mktl.elAt(\sl, 0).action = { |el| Ndef(\form).setUni(\freq, el.value) };
~mktl.elAt(\sl, 1).action = { |el| Ndef(\form).setUni(\shape, el.value) };
~mktl.elAt(\sl, 2).action = { |el| Ndef(\form).setUni(\ffreq, el.value) };
~mktl.elAt(\sl, 3).action = { |el| Ndef(\form).setUni(\amp, el.value * 0.25) };
)


(
// make a KtlLoop for them
~loop = KtlLoop(\kel);
// and tell it the object to control,
~loop.object = Ndef(\form);

// which params to record,
~loop.paramsToRecord_(~mktl.elAt(\sl).collect(_.name).keep(4));

// and which recorded controller params to map to which process params:
~loop.ktlMap_((sl_1: \freq, sl_2: \shape, sl_3: \ffreq, sl_4: \amp));

// and a gui
KtlLoopGui(~loop, 10, bounds: 330@180);
)

(
// connect the MKtl and the KtlLoop:
// use the common action of the group of all sliders to record
~mktl.elAt(\sl).action = { |el|
	var event = (\type: \set).put(el.name, el.value);
	"rec: ".post;
	event.postcs;
	~loop.recordEvent( event );
};
)

// for testing everything so far:
// start recording by code:
~loop.startRec;
// now move sliders ... then
~loop.stopRec;

// -> should get a recorded list info post:
// KtlLoop('kel') stopRec; // recorded list[2] with 145 events.

~loop.list; // a long list
~loop.list.size; // and its size

// should replay the movements that were recorded:
~loop.play;

// make the loop repeat by code:
~loop.looped = true;

(
// now, why not control rec play stop  from nanoKtl buttons:
~mktl.elAt(\tr, \rec).action = { |el| if (el.isOn) { ~loop.startRec; } };
~mktl.elAt(\tr, \stop).action = { |el| if (el.isOn) { ~loop.stop; } };
~mktl.elAt(\tr, \play).action = { |el| if (el.isOn) { ~loop.play; } };

// toggle playback looping with the cycle button
~mktl.elAt(\tr, \cycle).action =  { |el|
	if (el.isOn) { ~loop.looped = ~loop.looped.not }
};

// try that these all work...
)


(
// and step through recorded loop indices, left is down, right is up
~wrapStepListIndex = { |inc = 1|
	var next = (~loop.currIndex + inc)
	.wrap(0, ~loop.lists.lastIndex).postln;
	~loop.setList(next);
};
~mktl.elAt(\tr, \mleft).action =  { |el|
	if (el.isOn) { ~wrapStepListIndex.value(-1) }
};
~mktl.elAt(\tr, \mright).action =  { |el|
	if (el.isOn) { ~wrapStepListIndex.value(1) }
};
)


// while loop is playing, try rescaling the recorded movements:
// all recorded controller values can be scaled, shifted, and inverted.

~loop.rescaled = false;

~loop.rescaled = true;

// decrease range toward center
~loop.scaler = 0.5;
~loop.scaler = 0.25;
// shift up or down
~loop.shift = 0.1;
~loop.shift = -0.1;

// invert range
~loop.invert;
~loop.up;
~loop.flipInv;

// maybe not rescale amp?
~loop.dontRescale(\sl_4);
~loop.doRescale(\sl_4);

~loop.postKtlInfo;

