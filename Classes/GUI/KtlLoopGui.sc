KtlLoopGui : EventLoopGui {

		var resetScaleBut, rescaledBut, invBut, infoBut, scalerSl, shiftSl;


	// add sliders for scaler, shift,
	// but rescaled, inv, rstScale;
	// show ktlMap, paramsToRecord, nonRescalableCtls?

	makeViews { |options|
		super.makeViews(options);
		zone.decorator.nextLine.shift(0, 5);
		this.addScaleItems(zone.bounds.width - 8, skin.buttonHeight);
	}

	addScaleItems { |lineWidth, height|

		zone.decorator.nextLine;

		resetScaleBut = Button(zone, Rect(0,0,lineWidth * 0.2,height))
			.font_(font)
			.states_([[\rsScale]])
			.action_({ |but| object.resetLoop });

		zone.decorator.shift(5, 0);

		rescaledBut = Button(zone, Rect(0,0, lineWidth * 0.13,height))
			.font_(font)
			.states_([
				["norm", skin.fontColor, skin.offColor],
				["rscld", skin.fontColor, skin.onColor]
			])
			.action_({ object.rescaled_(object.rescaled.not); 	});

		invBut = Button(zone, Rect(0,0, lineWidth * 0.13,height))
			.font_(font)
			.states_([
				[\up, skin.fontColor, skin.offColor],
				[\inv, skin.fontColor, skin.onColor]
			])
			.action_({ |but| object.flip });

		zone.decorator.shift(10, 0);

		infoBut = Button(zone, Rect(0,0, lineWidth * 0.2,height))
			.font_(font)
			.states_([[\info]])
		.action_({ object.postKtlInfo });
		scalerSl = EZSlider(zone, lineWidth@height, \scale, [0.0, 4, \amp],
			{ |sl| object.scaler = sl.value }, 1, labelWidth: 40)
		.font_(font);
		scalerSl.view.resize_(2);

		shiftSl = EZSlider(zone, lineWidth@height, \shift, [-0.5, 0.5],
			{ |sl| object.shift = sl.value }, 0, labelWidth: 40)
		.font_(font);
		shiftSl.view.resize_(2);
	}

	getState {
		var newState = super.getState;
		if (object.notNil) {
			newState.put(\rescaled, object.rescaled.binaryValue);
			newState.put(\inverted, object.isInverse.binaryValue);
			newState.put(\scaler, object.scaler);
			newState.put(\shift, object.shift);
		};
		^newState
	}

	checkUpdate {
		var newState = this.getState;


		if (newState[\rescaled] != prevState[\rescaled]) {
			// this is dodgy - this button is nil on the
			// first update sometimes ... Heisenbug,
			// but the notNil check seems to fix it.
			if (rescaledBut.notNil) {
				rescaledBut.value_(newState[\rescaled]).refresh;
			};
		};
		if (newState[\inverted] != prevState[\inverted]) {
			invBut.value_(newState[\inverted]).refresh;
		};

		if (newState[\shift] != prevState[\shift]) {
			shiftSl.value_(newState[\shift]);
		};
		if (newState[\scaler] != prevState[\scaler]) {
			scalerSl.value_(newState[\scaler]);
		};

		// super.checkUpdate stores prevState
	//	prevState = newState;
		super.checkUpdate;

	}
}
