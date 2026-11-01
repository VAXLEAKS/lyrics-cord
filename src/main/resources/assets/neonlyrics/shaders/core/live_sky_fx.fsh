#version 330

// Lebendiger Himmel auf der Grafikkarte: dieselbe Rechnung wie LiveSky.color(), aber fuer jeden Bildpunkt.
// Werte kommen aus einer kleinen Textur (Sampler0, 16x1), die Zeit aus UV0.

uniform sampler2D Sampler0;

in vec3 skyDir;
in vec4 skyInfo;

out vec4 fragColor;

#moj_import <neonlyrics:sky_common.glsl>

// Die zwei aufwendigen Himmel (Weltraumschlacht, Riesen-DJ) - eigener Shader, damit die normalen Himmel schlank bleiben.

// ================================================================ WELTRAUMSCHLACHT
// Flotte 0 (blau) links, Flotte 1 (rot) rechts. Schiffe sind Formen mit Kanten-Licht (wirken plastisch):
// Jaeger mit gepfeilten Fluegeln und Cockpit, Grosskampfschiffe mit Bruecke, Panzerplatten, Fenstern,
// Geschuetztuermen und Schilden. Laser, Treffer, Feuerbaelle, Superstrahl, Truemmer und Planet im Hintergrund.
vec2 warShip(int team, int i, float t) {
    float fi = float(i), side = team == 0 ? -1. : 1.;
    float ph = fi * 1.9 + float(team) * 3.7;
    return vec2(side * (.16 + .30 * (.5 + .5 * sin(t * .21 + ph))) + .20 * sin(t * .37 + ph * 1.3),
                -.12 + fi * .085 + .16 * sin(t * .29 + ph * .7));
}
vec2 warHead(int team, int i, float t) {
    vec2 v = warShip(team, i, t + .05) - warShip(team, i, t);
    return length(v) > 1e-5 ? normalize(v) : vec2(team == 0 ? 1. : -1., 0.);
}
// Jaeger (zeigt nach +x, Laenge ~2): Rumpf, spitze Nase, gepfeilte Fluegel, Leitwerk
float warFighter(vec2 u) {
    float hull = fBox(u - vec2(-.1, 0.), vec2(.8, .15), .12);
    hull = min(hull, max(u.x - 1.05, max(.5 - u.x, abs(u.y) - .15 * (1.05 - u.x) / .55)));
    float wing = fBox(vec2(u.x + .2 + abs(u.y) * .6, abs(u.y) - .46), vec2(.2, .32), .05);
    float fin = fBox(vec2(u.x + .82, abs(u.y) - .2), vec2(.12, .05), .02);
    return min(min(hull, wing), fin);
}
// Grosskampfschiff (zeigt nach +x): Rumpf, Bug, Bruecke, Turm, Kiel, Triebwerksblock
float warCapital(vec2 u) {
    float h = fBox(u - vec2(-.1, 0.), vec2(.95, .13), .03);
    h = min(h, max(u.x - 1.3, max(.8 - u.x, abs(u.y) - .13 * (1.3 - u.x) / .5)));
    h = min(h, fBox(u - vec2(-.45, .18), vec2(.28, .06), .02));
    h = min(h, fBox(u - vec2(-.62, .28), vec2(.08, .06), .01));
    h = min(h, fBox(u - vec2(.1, -.17), vec2(.55, .05), .02));
    h = min(h, fBox(u - vec2(-1.08, 0.), vec2(.1, .1), .02));
    return h;
}
// plastisches Licht fuer eine flache Form: Normale aus der Steigung des Abstands
vec3 warShade(float s, vec2 g, vec3 base, vec3 tc, float scale) {
    float inner = clamp(-s * scale, 0., 1.);
    vec3 n = normalize(vec3(-g * (1. - inner) * 1.6, .45 + inner));
    vec3 L = normalize(vec3(-.55, .55, .65));
    float dif = clamp(dot(n, L), 0., 1.);
    float spec = pow(clamp(dot(reflect(-L, n), vec3(0., 0., 1.)), 0., 1.), 18.);
    return base * (.18 + 1.05 * dif) + vec3(1.) * spec * .45 + tc * pow(1. - inner, 6.) * .55;
}
vec3 warSky(vec3 d, float t) {
    float y = d.y, el = degrees(asin(clamp(y, -1., 1.)));
    vec3 col = fGrad(y, vec3(.30, .14, .36), vec3(.10, .07, .24), vec3(.03, .03, .10));
    float n1 = .5, n2 = .5;
    if (QLOW < .5) { n1 = sf3(d * 1.7 + 3., 2); n2 = sf3(d * 2.3 - 5., 2); }
    col += vec3(.55, .12, .25) * smoothstep(.45, .8, n1) * .38 + vec3(.12, .25, .65) * smoothstep(.5, .85, n2) * .38;
    col += vec3(fStars(d, 1.2, t)) * .9;
    // Sonne mit Glanz + Planet mit Mond
    vec3 sun = sdir(140., 48.);
    float sa = fAng(d, sun);
    col += vec3(1., .9, .75) * (exp(-sa * 30.) * .9 + exp(-sa * 6.) * .18);
    col += vec3(.8, .85, 1.) * exp(-abs(dot(d - sun, normalize(cross(sun, vec3(0., 1., 0.))))) * 200.) * exp(-sa * 3.) * .25;
    col = sPlanet(col, d, sdir(115., 8.), .26, vec3(.85, .45, .30), vec3(1., .45, .3), vec3(.6, .5, .6), 0.);
    col = sPlanet(col, d, sdir(95., 22.), .04, vec3(.7, .72, .8), vec3(.7, .8, 1.), vec3(.6, .5, .6), 0.);
    // ferne Gefechte ueberall im Himmel
    vec3 fc = floor(d * 7.);
    float fh = sh31(fc + 11.);
    float fp = fract(t * .23 + fh * 7.);
    col += vec3(1., .7, .4) * smoothstep(.06, 0., fp) * step(.84, fh) * smoothstep(.5, .1, length(fract(d * 7.) - .5)) * .55;

    vec3 C = sdir(180., 28.);
    float facing = dot(d, C);
    float flashAll = 0.;
    vec2 q = slocal(d, C);
    // alles Weitere nur im Schlachtfeld selbst (spart viel Arbeit, wenn man daneben schaut)
    if (facing > .25 && abs(q.x) < 1.95 && q.y > -.85 && q.y < .95) {
        float fade = smoothstep(.25, .45, facing);
        vec3 acc = vec3(0.);
        // Truemmer, die langsam durchs Bild treiben
        for (int k = 0; k < (QLOW > .5 ? 0 : 7); k++) {
            float fk = float(k);
            vec2 dp = vec2(fract(sh21(vec2(fk, 1.)) + t * (.008 + fk * .002)) * 2. - 1., sh21(vec2(fk, 7.)) * .8 - .35);
            float ang = t * (.3 + fk * .1);
            if (length(q - dp) > .05) continue;
            vec2 du = fRot(q - dp, vec2(cos(ang), sin(ang))) / (.012 + .01 * sh21(vec2(fk, 3.)));
            float rs = fBox(du, vec2(.9, .55), .3) + .2 * sin(du.x * 3. + fk) * sin(du.y * 4.);
            if (rs < 0.) col = vec3(.16, .14, .16) * (.6 + .5 * clamp(-du.x + du.y, -1., 1.));
        }
        // Grosskampfschiffe
        for (int team = 0; team < 2; team++) {
            float side = team == 0 ? -1. : 1.;
            vec2 cp = vec2(side * (.60 + .03 * sin(t * .1 + float(team))), .32 - float(team) * .12);
            float sc = .17;
            vec2 u = fRot(q - cp, vec2(-side, 0.)) / sc;
            float hull = warCapital(u);
            vec3 tc = team == 0 ? vec3(.3, .7, 1.) : vec3(1., .35, .25);
            if (hull < .02) {
                float e = .02;
                vec2 g = vec2(warCapital(u + vec2(e, 0.)) - warCapital(u - vec2(e, 0.)), warCapital(u + vec2(0., e)) - warCapital(u - vec2(0., e))) / (2. * e);
                vec3 base = mix(vec3(.30, .32, .38), vec3(.55, .57, .62), step(.5, fract(u.x * 2.3)) * .3 + .35);
                base *= .8 + .2 * step(.08, abs(fract(u.x * 7.) - .5)) * step(.04, abs(fract(u.y * 9.) - .5)); // Panzerplatten
                vec3 sh = warShade(hull, g, base, tc, 12.);
                // Fensterreihen + Positionslichter
                float win = step(.55, sh21(floor(vec2(u.x * 22., u.y * 14.)) + float(team) * 9.)) * step(abs(u.y), .1) * step(.4, fract(u.y * 14.));
                sh += vec3(1., .85, .6) * win * .7;
                sh += tc * step(.94, fract(u.x * 3. - t * .5)) * step(abs(u.y + .02), .03) * 1.5;
                col = mix(col, sh, smoothstep(.02, -.01, hull));
            }
            acc += tc * exp(-max(hull, 0.) * 14.) * .12;
            // Triebwerke (zwei Duesen) mit Flammen
            for (int k = 0; k < 2; k++) {
                vec2 nz = vec2(-1.2, k == 0 ? .05 : -.05);
                float fl = length((u - nz) * vec2(.35, 1.));
                acc += mix(vec3(1.), tc, .5) * (exp(-fl * 14.) * 1.3 + exp(-fl * 4.) * .25) * (.85 + .15 * sin(t * 30. + float(k)));
            }
            // Geschuetztuerme feuern Salven
            for (int k = 0; k < 3; k++) {
                vec2 tp = vec2(-.1 + float(k) * .3, .14);
                float fz = step(.85, fract(t * .9 + float(k) * .31 + float(team) * .5));
                acc += vec3(1., .9, .7) * exp(-length(u - tp) * 25.) * fz * 1.5;
            }
        }
        // Superstrahl + Schild-Treffer
        float bc = fract(t / 9.), beam = smoothstep(0., .03, bc) * (1. - smoothstep(.16, .22, bc));
        int shooter = int(mod(floor(t / 9.), 2.));
        vec2 a = shooter == 0 ? vec2(-.42, .32) : vec2(.42, .20), b = shooter == 0 ? vec2(.60, .20) : vec2(-.60, .32);
        if (beam > 0.) {
            float bd = fSeg(q, a, b);
            float w = .006 + .004 * sin(t * 40.);
            vec3 bcol = shooter == 0 ? vec3(.55, .9, 1.) : vec3(1., .55, .35);
            acc += bcol * (exp(-bd / w) * 2. + exp(-bd * 16.) * .45) * beam;
            acc += bcol * exp(-length(q - a) * 30.) * beam * 2.;
            // Schild-Blase um das Ziel mit Waben
            vec2 sq = (q - b) / vec2(.2, .1);
            float sr = length(sq);
            vec2 hx = sq * 9.;
            float hex = smoothstep(.85, 1., max(abs(sin(hx.x)), abs(sin(hx.x * .5 + hx.y * .866))));
            acc += bcol * (exp(-pow((sr - 1.) * 9., 2.)) * .9 + step(sr, 1.) * hex * .25) * beam;
            flashAll += beam * .5;
        }
        // Jaeger beider Flotten
        for (int team = 0; team < 2; team++) {
            vec3 tc = team == 0 ? vec3(.35, .75, 1.) : vec3(1., .40, .25);
            for (int i = 0; i < 6; i++) {
                vec2 p = warShip(team, i, t), p2 = warShip(team, i, t + .05);
                vec2 h = length(p2 - p) > 1e-5 ? normalize(p2 - p) : vec2(team == 0 ? 1. : -1., 0.);
                float sc = .048;
                vec2 u = fRot(q - p, h) / sc;
                if (dot(u, u) > 150.) continue;
                float s = warFighter(u);
                // Triebwerks-Spur
                float tr = exp(-u.y * u.y * 5.) * smoothstep(-12., -.8, u.x) * step(u.x, -.85);
                acc += tc * tr * .35;
                acc += mix(vec3(1.), tc, .4) * exp(-length(u - vec2(-.95, 0.)) * 2.2) * 1.1;
                if (s < .05) {
                    float e = .03;
                    vec2 g = vec2(warFighter(u + vec2(e, 0.)) - warFighter(u - vec2(e, 0.)), warFighter(u + vec2(0., e)) - warFighter(u - vec2(0., e))) / (2. * e);
                    vec3 base = vec3(.62, .64, .70);
                    base = mix(base, tc * .8, step(abs(abs(u.y) - .46), .05) * step(-.5, u.x)); // Team-Streifen auf den Fluegeln
                    vec3 sh = warShade(s, g, base, tc, 8.);
                    float cp = length((u - vec2(.35, 0.)) * vec2(1., 2.2));
                    sh = mix(sh, vec3(.1, .2, .3) + vec3(.5, .8, 1.) * smoothstep(.2, 0., cp) * .6, smoothstep(.22, .18, cp)); // Cockpit
                    col = mix(col, sh, smoothstep(.05, -.02, s));
                }
                acc += tc * exp(-max(s, 0.) * 3.) * .04;
            }
        }
        // Raketen mit Rauchspur auf Bogenbahnen zwischen den Grosskampfschiffen
        for (int k = 0; k < (QLOW > .5 ? 0 : 4); k++) {
            float fk = float(k), per = 4.5 + fk;
            float age = fract(t / per + fk * .3);
            vec2 a = k % 2 == 0 ? vec2(-.52, .32) : vec2(.52, .2);
            vec2 b = k % 2 == 0 ? vec2(.6, .2 + .04 * fk) : vec2(-.6, .32 - .03 * fk);
            vec2 ctrl = (a + b) * .5 + vec2(0., .3 + .08 * fk);
            vec2 mh = mix(mix(a, ctrl, age), mix(ctrl, b, age), age);
            if (length(q - mh) > .3 && length(q - b) > .15) continue; // weit weg von Rakete und Ziel: nichts zu zeichnen
            for (int s = 0; s < 9; s++) {
                float u0 = age - float(s) * .022;
                if (u0 < 0.) break;
                vec2 pp = mix(mix(a, ctrl, u0), mix(ctrl, b, u0), u0);
                float fs = float(s) / 9.;
                acc += vec3(.55, .5, .55) * exp(-length(q - pp) / (.006 + fs * .02)) * .16 * (1. - fs);
            }
            vec2 mp = mix(mix(a, ctrl, age), mix(ctrl, b, age), age);
            acc += vec3(1., .8, .5) * (exp(-length(q - mp) * 140.) * 2. + exp(-length(q - mp) * 30.) * .3) * step(age, .97);
            acc += vec3(1., .7, .4) * exp(-length(q - b) * 40.) * smoothstep(.95, 1., age) * 2.;
        }
        // Flaggschiff im Vordergrund: riesig, zieht langsam unten durchs Bild
        {
            vec2 hp0 = vec2(mod(t * .02, 3.6) - 1.8, -.44);
            float sc = .42;
            vec2 u = (q - hp0) / sc;
            if (abs(u.x) < 1.7 && abs(u.y) < .55) {
                float hull = warCapital(u);
                vec3 tc = vec3(.3, .75, 1.);
                if (hull < .02) {
                    float e = .01;
                    vec2 g = vec2(warCapital(u + vec2(e, 0.)) - warCapital(u - vec2(e, 0.)), warCapital(u + vec2(0., e)) - warCapital(u - vec2(0., e))) / (2. * e);
                    vec2 gc = floor(u * vec2(20., 26.));
                    float greeb = sh21(gc);
                    vec3 base = vec3(.42, .44, .5) * (.75 + .35 * greeb);
                    base *= .8 + .2 * step(.1, abs(fract(u.x * 20.) - .5)) * step(.1, abs(fract(u.y * 26.) - .5)); // Aufbauten-Raster
                    vec3 shc = warShade(hull, g, base, tc, 20.);
                    shc += vec3(1., .85, .6) * step(.72, sh21(gc + 3.)) * step(abs(u.y), .1) * step(.5, fract(u.y * 26.)) * .9; // Fenster
                    shc += tc * step(.96, fract(u.x * 2. - t * .4)) * step(abs(u.y + .17), .04) * 2.; // Lauflicht am Kiel
                    // Schaden: brennende Stellen
                    float burn = smoothstep(.75, .9, sf2(u * 3. + 7., 3));
                    shc = mix(shc, vec3(1., .45, .15) * (.6 + .4 * sin(t * 9. + u.x * 20.)), burn * step(hull, 0.) * .8);
                    col = mix(col, shc, smoothstep(.01, -.005, hull));
                }
                acc += tc * exp(-max(hull, 0.) * 22.) * .15;
                for (int k = 0; k < 3; k++) {
                    vec2 nz = vec2(-1.2, (float(k) - 1.) * .07);
                    float fl = length((u - nz) * vec2(.3, 1.));
                    acc += mix(vec3(1.), tc, .45) * (exp(-fl * 16.) * 1.4 + exp(-fl * 4.) * .3) * (.85 + .15 * sin(t * 30. + float(k)));
                }
            }
        }
        // Laserbolzen: jeder Jaeger schiesst auf einen Gegner
        for (int k = 0; k < 12; k++) {
            int team = k < 6 ? 0 : 1, i = k % 6, j = (i * 2 + 1 + k / 6) % 6;
            float hk = sh21(vec2(float(k), 3.));
            float ph = fract(t * (.9 + hk * .5) + hk * 5.);
            vec2 pa = warShip(team, i, t), pb0 = warShip(1 - team, j, t);
            vec2 pb = mix(pa, pb0, ph);
            if (length(q - pb) > .2 && length(q - pb0) > .1) continue;
            vec2 dir = normalize(pb0 - pa + 1e-5);
            float bd = fSeg(q, pb - dir * .06, pb);
            vec3 lc = team == 0 ? vec3(.4, 1., .9) : vec3(1., .35, .3);
            acc += lc * (exp(-bd / .002) * 1.6 + exp(-bd * 70.) * .3) * step(ph, .92);
            float hit = smoothstep(.9, .97, ph) * (1. - smoothstep(.97, 1., ph));
            acc += vec3(1., .85, .6) * exp(-length(q - pb0) * 80.) * hit * 2.2;
        }
        // Explosionen
        for (int e = 0; e < 4; e++) {
            float fe = float(e), per = 3.1 + fe * .7;
            float cyc = floor(t / per + fe * .37), age = fract(t / per + fe * .37);
            vec2 ep = vec2(sh21(vec2(cyc, fe)) - .5, sh21(vec2(fe, cyc + 4.)) * .7 - .25) * vec2(1.3, 1.);
            float r = length(q - ep);
            float life = smoothstep(0., .02, age) * (1. - smoothstep(.35, .6, age));
            flashAll += exp(-age * 12.) * life * .25;
            if (r > .45) continue;
            float fire = exp(-r / (.025 + age * .10)) * (1. - age) * (1. - age) * (1. + .6 * sn2(vec2(atan(q.y - ep.y, q.x - ep.x) * 3., cyc)));
            float ring = exp(-pow((r - age * .16) / .025, 2.)) * pow(1. - age, 3.) * life * .5;
            acc += mix(vec3(1., .95, .7), vec3(1., .35, .1), smoothstep(.0, .3, age)) * fire * 2.2 * life;
            acc += vec3(.7, .85, 1.) * ring * .9;
            float ang = atan(q.y - ep.y, q.x - ep.x);
            float sp = step(.82, sh21(vec2(floor(ang * 9.), cyc + fe))) * exp(-pow((r - age * .16) / .004, 2.));
            acc += vec3(1., .7, .4) * sp * (1. - age) * life * 1.5;
        }
        col += acc * fade;
    }
    col += vec3(1., .85, .7) * flashAll * .12;
    col *= 1. - clamp(-el / 8., 0., 1.) * .5;
    return col;
}

// ================================================================ RIESEN-DJ (3D)
// Club-Buehne wie bei einem echten Set: LED-Wand mit den anderen Himmeln als Visuals, Neon-Roehren,
// Traverse mit Moving Heads (dicke Lichtkegel im Nebel), Blinder, Strobo, Laser, vorne die Crowd.
// Fuenf DJs zur Auswahl (STYLE): 0 Neon (weisser Hoodie, Cap), 1 Robo (Chrom, LED-Visier), 2 Astro (Raumanzug,
// Goldvisier), 3 Cube (leuchtender Pixel-Wuerfelkopf), 4 Gold (rote Jacke, Bucket Hat, dicke Kette).
// Der DJ tanzt im erkannten Takt: zehn Moves, alle vier Schlaege ein anderer, weich uebergeblendet.
// Drop (Anfang jeder 8-Takt-Phrase): Arme weit auf, Feuersaeulen vorne an der Buehne, Strobo, Blinder.
// LAYER 1 = nur Buehne/Licht (hinter den Lyrics), LAYER 2 = nur DJ, Pult, Crowd, Feuer (vor den Lyrics).
vec3 jHead, jNeck, jChest, jHip, jSL, jSR, jEL, jER, jHL, jHR, jFL, jFR;

float smin(float a, float b, float k) { float h = clamp(.5 + .5 * (b - a) / k, 0., 1.); return mix(b, a, h) - k * h * (1. - h); }
float sdCap3(vec3 p, vec3 a, vec3 b, float r) { vec3 pa = p - a, ba = b - a; float h = clamp(dot(pa, ba) / dot(ba, ba), 0., 1.); return length(pa - ba * h) - r; }
float sdBox3(vec3 p, vec3 b, float r) { vec3 q = abs(p) - b + r; return length(max(q, 0.)) + min(max(q.x, max(q.y, q.z)), 0.) - r; }
float sdCyl(vec3 p, float r, float h) { vec2 d = abs(vec2(length(p.xz), p.y)) - vec2(r, h); return min(max(d.x, d.y), 0.) + length(max(d, 0.)); }
float sdEll(vec3 p, vec3 r) { float k0 = length(p / r), k1 = length(p / (r * r)); return k0 * (k0 - 1.) / max(k1, 1e-4); }

// Zwei-Knochen-Gelenk: Ellbogen so, dass Ober- und Unterarm ihre Laenge behalten
vec3 djIK(vec3 s, inout vec3 h, float l1, float l2, vec3 pole) {
    vec3 d = h - s;
    float L = clamp(length(d), .1, l1 + l2 - .02);
    vec3 dir = normalize(d);
    h = s + dir * L;
    float a = (l1 * l1 - l2 * l2 + L * L) / (2. * L);
    float hg = sqrt(max(l1 * l1 - a * a, 0.));
    vec3 pp = pole - dir * dot(pole, dir);
    pp = length(pp) > 1e-4 ? normalize(pp) : vec3(0., -1., 0.);
    return s + dir * a + pp * hg;
}

// Schlaege seit Phrasen-Anfang (0..32) -> Drop in den ersten Schlaegen
float djDrop() { return MUSIC * (1. - smoothstep(0., 3.5, BIX + BPH)); }

// Haende, Sprung, Nicken fuer Move m bei Phase ph, Schlag k im Takt
void djMove(int m, float ph, float k, out vec3 hl, out vec3 hr, out float jump, out float nod) {
    float hit = exp(-ph * 7.), bnc = sin(ph * 3.14159), sw = sin(ph * 6.2832);
    float odd = mod(k, 2.);
    jump = 0.;
    nod = .3 * hit;
    vec3 deckL = vec3(-1.45, -.55, .95), fader = vec3(.2 * sin((k + ph) * 1.5708), -.58, 1.05);
    if (m == 0) {        // Nicken + Scratchen + Fader
        hl = deckL + vec3(.22 * sin(ph * 12.566), 0., .08 * cos(ph * 12.566));
        hr = fader;
    } else if (m == 1) { // Fist-Pump
        hl = deckL + vec3(.1 * sw, 0., 0.);
        hr = vec3(.75, 1.45 + 1.05 * hit, .5);
        nod = .42 * hit;
    } else if (m == 2) { // Klatschen ueber dem Kopf
        hl = vec3(-.12 - .5 * bnc, 2.55 + .12 * hit, .45);
        hr = vec3(.12 + .5 * bnc, 2.55 + .12 * hit, .45);
        jump = .12 * bnc;
    } else if (m == 3) { // Springen, Haende am Pult
        hl = vec3(-1.05, -.52, 1.05);
        hr = vec3(1.05, -.52, 1.05);
        jump = .55 * bnc;
        nod = .2 * hit;
    } else if (m == 4) { // Kopfhoerer festhalten + Fader
        hl = vec3(-.62, 1.65, .05);
        hr = fader + vec3(.3, 0., 0.);
        nod = .38 * hit;
    } else if (m == 5) { // Beide Arme hoch, wedeln
        float s2 = sin((k + ph) * 3.14159);
        hl = vec3(-.7 + .55 * s2, 2.4 + .15 * hit, .6);
        hr = vec3(.7 + .55 * s2, 2.4 + .15 * hit, .6);
        jump = .18 * bnc;
    } else if (m == 6) { // An den Knoepfen drehen
        hl = vec3(-.32 + .06 * cos(ph * 6.2832), -.56, 1.05 + .06 * sin(ph * 6.2832));
        hr = vec3(.32 + .06 * sin(ph * 6.2832), -.56, 1.05 + .06 * cos(ph * 6.2832));
        nod = .45 * hit;
    } else if (m == 7) { // Abwechselnd nach oben zeigen
        vec3 up = vec3(.55, 2.7 + .15 * hit, .55), low = vec3(1.1, -.52, 1.);
        hr = mix(up, low, odd);
        hl = mix(vec3(-1.1, -.52, 1.), vec3(-.55, 2.7 + .15 * hit, .55), odd);
        jump = .1 * bnc;
    } else if (m == 8) { // Arme weit auf (Drop)
        hl = vec3(-2.0, 1.25 + .2 * hit, .3);
        hr = vec3(2.0, 1.25 + .2 * hit, .3);
        nod = -.25 + .2 * hit;
        jump = .2 * bnc;
    } else {             // Doppel-Faust abwechselnd hoch/tief
        hl = vec3(-.7, mix(.6, 1.9, odd) + .3 * hit, .7);
        hr = vec3(.7, mix(1.9, .6, odd) + .3 * hit, .7);
        jump = .15 * bnc;
    }
}

void djPose(float t) {
    float ph = BPH, k = mod(BIX, 4.);
    float bar = floor(BIX / 4.);
    int m = int(mod(bar * 3. + floor(bar / 8.) * 5., 10.)), mp = int(mod((bar - 1.) * 3. + floor((bar - 1.) / 8.) * 5. + 10., 10.));
    float dropT = BIX + ph;
    if (dropT < 3.) m = 8;          // Drop: Arme weit auf
    else if (dropT < 4.) mp = 8;
    vec3 hl, hr, hl0, hr0;
    float jump, nod, jump0, nod0;
    djMove(m, ph, k, hl, hr, jump, nod);
    djMove(mp, ph, k, hl0, hr0, jump0, nod0);
    float w = k < .5 ? smoothstep(0., 1., ph) : 1.;
    hl = mix(hl0, hl, w); hr = mix(hr0, hr, w); jump = mix(jump0, jump, w); nod = mix(nod0, nod, w);
    float br = .03 * sin(t * 1.3);
    vec3 il = vec3(-.95, -.55 + br * .5, .9), ir = vec3(.95, -.55 + br * .5, .9);
    float M = smoothstep(0., 1., MUSIC);
    hl = mix(il, hl, M); hr = mix(ir, hr, M);
    jump *= M;
    float hit = exp(-ph * 7.) * M;
    nod = mix(.05 * sin(t * .7), nod, M);
    float sway = M * .08 * sin((k + ph) * 3.14159);
    float tilt = M * .06 * sin((k + ph) * 3.14159 + 1.);
    float turn = (1. - M) * .25 * sin(t * .35);
    jHip = vec3(sway * .6, -.75 + jump, 0.);
    jChest = vec3(sway, .42 + jump + br - .05 * hit, .05 + nod * .25);
    jNeck = jChest + vec3(0., .74, .02);
    jHead = jNeck + vec3(sin(turn) * .12 + tilt, .5 * cos(nod), .5 * sin(nod) + .06);
    jSL = jChest + vec3(-.72, .5 + .05 * hit, 0.); jSR = jChest + vec3(.72, .5 + .05 * hit, 0.);
    hl.y += jump * step(0., hl.y); hr.y += jump * step(0., hr.y);
    jEL = djIK(jSL, hl, .8, .78, vec3(-.6, -.6, -.5));
    jER = djIK(jSR, hr, .8, .78, vec3(.6, -.6, -.5));
    jHL = hl; jHR = hr;
    jFL = normalize(jHL - jEL); jFR = normalize(jHR - jER);
}

// Hand: Handflaeche + vier Finger + Daumen (f = Richtung des Unterarms)
float djHand(vec3 p, vec3 h, vec3 f, float side) {
    if (length(p - h) > .5) return length(p - h) - .3;
    vec3 sv = normalize(cross(f, abs(f.y) > .9 ? vec3(0., 0., 1.) : vec3(0., 1., 0.)));
    vec3 c = h + f * .1;
    float d = sdEll(p - c, vec3(.13, .13, .13));
    d = smin(d, sdCap3(p, c - f * .05, c + f * .1, .1), .05);
    for (int i = 0; i < 4; i++) {
        float o = (float(i) - 1.5) * .055;
        vec3 b = c + f * .14 + sv * o;
        d = smin(d, sdCap3(p, b, b + f * (.12 - abs(o) * .3) + vec3(0., -.03, 0.), .032), .02);
    }
    vec3 tb = c + sv * side * .12;
    d = smin(d, sdCap3(p, tb, tb + f * .1 + sv * side * .05, .036), .02);
    return d;
}

vec2 djDesk(vec3 p) {
    vec2 r = vec2(sdBox3(p - vec3(0., -1.3, 1.), vec3(2.6, .58, .65), .05), 4.);
    float pl = min(sdCyl(p - vec3(-1.45, -.66, 1.), .5, .04), sdCyl(p - vec3(1.45, -.66, 1.), .5, .04));
    if (pl < r.x) r = vec2(pl, 5.);
    float mx = sdBox3(p - vec3(0., -.67, 1.05), vec3(.55, .05, .44), .02);
    if (mx < r.x) r = vec2(mx, 6.);
    float lap = sdBox3(p - vec3(0., -.45, .55), vec3(.42, .26, .015), .01);
    if (lap < r.x) r = vec2(lap, 11.);
    return r;
}

// Materialien: 1 Haut, 2 Kleidung, 3 Kopfhoerer, 4 Pult, 5 Plattenteller, 6 Mischpult, 7 Brille, 8 Hut, 9 Gold, 10 Bart,
// 11 Laptop, 12 Chrom, 13 LED-Visier, 14 weisser Kunststoff, 15 Goldvisier, 16 LED-Wuerfel
vec2 djMap(vec3 p) {
    vec2 dk = djDesk(p);
    float hull = sdCap3(p, jHip - vec3(0., .3, 0.), jHead + vec3(0., .4, 0.), 1.3);
    hull = min(hull, min(length(p - jHL), length(p - jHR)) - .45);
    if (hull > .25) return vec2(min(dk.x, hull), dk.y);
    bool robo = STYLE > .5 && STYLE < 1.5, astro = STYLE > 1.5 && STYLE < 2.5, cube = STYLE > 2.5 && STYLE < 3.5;
    vec3 cp = p - jChest;
    cp.x *= 1. + .2 * clamp(-cp.y, 0., 1.2);
    float body = smin(sdBox3(cp, vec3(.55, .58, .3), .3), sdCap3(p, jHip, jChest, .5), .25);
    body = smin(body, min(length(p - jSL) - .26, length(p - jSR) - .26), .12);
    body = smin(body, min(sdCap3(p, jNeck, jSL, .13), sdCap3(p, jNeck, jSR, .13)), .1);
    float arms = min(min(sdCap3(p, jSL, jEL, .19), sdCap3(p, jEL, jHL - jFL * .05, .15)), min(sdCap3(p, jSR, jER, .19), sdCap3(p, jER, jHR - jFR * .05, .15)));
    body = smin(body, arms, .1);
    if (astro) body = min(body, sdBox3(p - jChest - vec3(0., .12, -.45), vec3(.42, .45, .16), .08)); // Rucksack
    vec2 r = vec2(body, robo ? 12. : astro ? 14. : 2.);
    float hands = min(djHand(p, jHL, jFL, 1.), djHand(p, jHR, jFR, -1.));
    if (hands < r.x) r = vec2(hands, robo ? 12. : astro ? 14. : cube ? 2. : 1.);
    vec3 hp = p - jHead;
    float neck = sdCap3(p, jNeck - vec3(0., .15, 0.), jNeck + vec3(0., .25, 0.), .17);
    if (robo) {
        float head = min(sdBox3(hp, vec3(.38, .36, .36), .15), neck);
        if (head < r.x) r = vec2(head, 12.);
        float vis = sdBox3(hp - vec3(0., .02, .3), vec3(.3, .12, .08), .06);
        float ant = min(sdCap3(hp, vec3(.2, .3, 0.), vec3(.3, .62, 0.), .025), length(hp - vec3(.3, .66, 0.)) - .06);
        if (min(vis, ant) < r.x) r = vec2(min(vis, ant), 13.);
    } else if (astro) {
        float hel = min(length(hp - vec3(0., .02, 0.)) - .58, neck);
        if (hel < r.x) r = vec2(hel, 14.);
        float vis = max(max(length(hp - vec3(0., .02, .03)) - .575, .18 - hp.z), abs(hp.y) - .3);
        if (vis < r.x) r = vec2(vis, 15.);
    } else if (cube) {
        float cb = min(sdBox3(hp, vec3(.42), .07), neck);
        if (cb < r.x) r = vec2(cb, 16.);
    } else {
        // Mensch: Kopf, Kiefer, Nase
        float head = sdEll(hp, vec3(.38, .45, .41));
        head = smin(head, sdBox3(hp - vec3(0., -.25, .1), vec3(.2, .1, .22), .12), .12);
        head = smin(head, sdCap3(hp, vec3(0., .02, .38), vec3(0., -.08, .45), .055), .04);
        float skin = smin(head, neck, .08);
        if (skin < r.x) r = vec2(skin, STYLE > 3.5 && hp.y < -.12 && hp.z > .05 && head < neck ? 10. : 1.);
        float hat;
        if (STYLE < .5) {   // Cap mit Schirm
            hat = max(sdEll(hp - vec3(0., .1, -.02), vec3(.43, .4, .45)), -(hp.y - .1));
            hat = min(hat, sdBox3(hp - vec3(0., .12, .48), vec3(.3, .02, .17), .02));
        } else {            // Bucket Hat
            hat = max(sdEll(hp - vec3(0., .12, 0.), vec3(.44, .42, .44)), -(hp.y - .12));
            hat = min(hat, max(abs(hp.y - .1) - .025, length(hp.xz) - .66));
        }
        if (hat < r.x) r = vec2(hat, 8.);
        float gl = sdBox3(hp - vec3(0., .05, .37), vec3(.3, .065, .05), .035);
        if (gl < r.x) r = vec2(gl, 7.);
    }
    if (!astro) {   // Kopfhoerer (der Astronaut hat den Helm)
        float hs = cube ? 1.18 : 1.;
        vec3 hq = hp / hs;
        float band = max(length(vec2(length(hq.xy - vec2(0., .02)) - .52, hq.z + .02)) - .055, -(hq.y + .05));
        float cups = min(sdCap3(hq, vec3(-.42, -.04, 0.), vec3(-.56, -.04, 0.), .19), sdCap3(hq, vec3(.42, -.04, 0.), vec3(.56, -.04, 0.), .19));
        float hph = min(band, cups) * hs;
        if (hph < r.x) r = vec2(hph, 3.);
        vec3 ch = p - jChest - vec3(0., .38, .27);
        float chain = max(length(vec2(length(ch.xy * vec2(1., 1.6)) - .28, ch.z)) - (STYLE > 3.5 ? .05 : .03), ch.y);
        if (chain < r.x) r = vec2(chain, 9.);
    }
    if (dk.x < r.x) r = dk;
    return r;
}

// Normale mit einer Schleife (der Compiler kopiert djMap so nur einmal hinein)
vec3 djNormal(vec3 p) {
    vec3 n = vec3(0.);
    for (int i = 0; i < 4; i++) {
        vec3 e = .5773 * (2. * vec3(float(((i + 3) >> 1) & 1), float((i >> 1) & 1), float(i & 1)) - 1.);
        n += e * djMap(p + e * .003).x;
    }
    return normalize(n);
}

float djShadow(vec3 p, vec3 l) {
    float s = 1., t = .04;
    for (int i = 0; i < 10; i++) {
        float h = djMap(p + l * t).x;
        s = min(s, 8. * h / t);
        t += clamp(h, .06, .35);
        if (s < .02 || t > 3.) break;
    }
    return clamp(s, 0., 1.);
}

// Visual auf der LED-Wand: einer der anderen Himmel, durch eine kleine Kamera gesehen
vec3 djWall(vec2 uv, int vi, float t) {
    float az = 0., el = 18.;
    if (vi == 1) { az = 200.; el = 14.; } else if (vi == 2) { az = 165.; el = 22.; } else if (vi == 3) { az = 160.; el = 14.; }
    else if (vi == 4) { az = 30.; el = 25.; } else if (vi == 5) { az = 180.; el = 14.; } else if (vi == 6) { az = 100.; el = 12.; }
    else if (vi == 7) { az = 140.; el = 25.; }
    vec3 f = sdir(az + 6. * sin(t * .1), el), r = normalize(cross(f, vec3(0., 1., 0.))), u = cross(r, f);
    vec3 vd = normalize(f + (uv.x - .5) * 1.1 * 1.78 * r + (uv.y - .5) * 1.1 * u);
    return calmSky(vd, vi, t);
}

// Feuersaeulen vorne an der Buehne (Drop). Liefert Farbe (vormultipliert) und Deckkraft.
vec4 djFire(vec2 q, float t, float fk) {
    if (fk < .01 || q.y < -2.2 || q.y > 3.4) return vec4(0.);
    vec4 acc = vec4(0.);
    for (int i = 0; i < 6; i++) {
        float xj = (i < 3 ? -1. : 1.) * (2.95 + mod(float(i), 3.) * 1.2);
        vec2 lp = q - vec2(xj, -1.95);
        float H = (3.4 + 1.8 * sh21(vec2(float(i), floor(t * 3.)))) * fk;
        if (lp.y < -.2 || lp.y > H + 1. || abs(lp.x) > 1.5) continue;
        float y01 = clamp(lp.y / max(H, .01), 0., 1.);
        float n = sf2(vec2(lp.x * 2.6 + xj, lp.y * 1.3 - t * 6.5), 3);
        float w = .22 + .55 * y01;
        float body = smoothstep(w, w * .25, abs(lp.x + (n - .5) * .5 * y01)) * smoothstep(H * 1.05, H * .45, lp.y + (n - .5) * .9) * step(-.05, lp.y);
        vec3 fc = mix(vec3(1., .9, .55), vec3(1., .42, .06), smoothstep(.0, .35, y01));
        fc = mix(fc, vec3(.75, .12, .04), smoothstep(.55, 1., y01));
        float glow = exp(-length(vec2(lp.x * .8, (lp.y - H * .4) * .35)) * 2.) * .35;
        acc.rgb += fc * (body * 1.6 + glow) * fk;
        acc.a = max(acc.a, clamp(body * 1.2 + glow, 0., 1.) * fk);
    }
    return acc;
}

vec3 djSky(vec3 d, float t) {
    float y = d.y, el = degrees(asin(clamp(y, -1., 1.)));
    float M = MUSIC, B = BEAT * M;
    float ph = BPH, bnc = sin(ph * 3.14159) * M;
    float drop = djDrop();
    float strobe = drop * step(.5, fract(ph * 4.));
    vec3 hue = .55 + .45 * cos(6.2831 * (vec3(0., .33, .67) + t * .05 + floor(BIX / 4.) * .17 * M));
    vec3 pink = vec3(1., .25, .55), cyan = vec3(.25, .8, 1.);
    vec3 col = vec3(0.);
    if (LAYER < 1.5) {
        col = fGrad(y, vec3(.32, .08, .32), vec3(.10, .03, .18), vec3(.03, .01, .07));
        col += vec3(fStars(d, .6, t)) * .5 * smoothstep(.3, .7, y);
    }
    vec3 C = sdir(180., 20.);
    float facing = dot(d, C);
    if (facing > .3) {
        vec3 R = normalize(cross(C, vec3(0., 1., 0.))), U = cross(R, C);
        float D = 7.4;
        vec2 q = vec2(dot(d, R), dot(d, U)) / facing * D;
        float fade = smoothstep(.3, .5, facing);
        vec3 wallTint = hue;
        if (LAYER < 1.5) {
            vec3 st = col;
            // ---- LED-Wand: zeigt die anderen Himmel als Visuals
            if (abs(q.x) < 3.4 && q.y > -.75 && q.y < 3.05) {
                vec2 uv = vec2((q.x + 3.4) / 6.8, (q.y + .75) / 3.8);
                float slot = M > .5 ? floor(BIX / 8.) : floor(t / 7.);
                vec3 led = djWall(uv, int(mod(slot, 8.)), t);
                float since = M > .5 ? (mod(BIX, 8.) + ph) : fract(t / 7.) * 7.;
                float gl = exp(-since * 3.);
                led = mix(led, led.gbr, step(.5, fract(uv.y * 14. + t * 3.)) * gl);
                led += vec3(1.) * gl * .35;
                vec2 g = vec2(q.x * 1.6 + sin(q.y * 1.4 + t * .9) * .5, q.y * 1.6 + sin(q.x * 1.1 - t * .7) * .6);
                vec2 fg = abs(fract(g) - .5);
                led += mix(vec3(1., .45, .15), vec3(1., .2, .7), uv.y) * smoothstep(.05, .0, .5 - max(fg.x, fg.y)) * B * .6;
                led += hue * exp(-pow((length(q - vec2(0., 1.2)) - ph * 3.4) * 5., 2.)) * (1. - ph) * M * .6;
                led *= (.85 + .35 * B) * (.78 + .22 * step(.16, fract(q.x * 10.)) * step(.16, fract(q.y * 10.)));
                wallTint = clamp(led, 0., 1.5);
                st = led;
            }
            st += vec3(.35, .35, .4) * smoothstep(.06, 0., abs(max(abs(q.x) - 3.4, max(q.y - 3.05, -.75 - q.y))));
            // ---- Neon-Roehren
            for (int i = 0; i < 6; i++) {
                float fi = float(i);
                float xi = (i < 3 ? -1. : 1.) * (3.85 + mod(fi, 3.) * .8);
                float on = .55 + .45 * step(.5, fract(BIX * .25 + fi * .37)) * M;
                float tube = exp(-abs(q.x - xi) * 18.) * 1.5 + exp(-abs(q.x - xi) * 2.5) * .25;
                tube *= smoothstep(-2.2, -1.9, q.y) * smoothstep(3.7, 3.4, q.y);
                st += mix(pink, vec3(1.), exp(-abs(q.x - xi) * 40.)) * tube * on * (.7 + .6 * B + drop);
            }
            // ---- Nebel (wird von den Scheinwerfern angestrahlt)
            float haze = .55 + .45 * sf2(q * .5 + vec2(t * .07, -t * .03), 2);
            // ---- Traverse mit Moving Heads: dicke Lichtkegel von oben, die sich kreuzen (wie im Club)
            float truss = smoothstep(.06, 0., abs(q.y - 3.6) - .14) * step(abs(q.x), 5.6);
            st = mix(st, vec3(.08, .08, .1) + vec3(.06) * step(.5, fract((q.x + q.y) * 4.)), truss);
            for (int i = 0; i < 8; i++) {
                float fi = float(i);
                vec2 org = vec2(-4.6 + fi * 1.314, 3.45);
                float side = fi < 4. ? 1. : -1.;
                float ang = 3.14159 + side * (.25 + .35 * sin(t * (.45 + fi * .07) + fi * 1.3 + floor(BIX / 4.) * 1.7 * M)) * (.4 + .6 * M);
                vec2 dir = vec2(sin(ang), cos(ang));
                vec2 rp = q - org;
                float along = dot(rp, dir), al = max(along, 0.);
                float perp = abs(dot(rp, vec2(dir.y, -dir.x)));
                float core = exp(-perp / (.03 + al * .035));
                float soft = exp(-perp / (.12 + al * .16)) * .4;
                float beam = (core + soft) * step(0., along) * exp(-al * .09) * haze;
                vec3 bcol = i % 3 == 0 ? vec3(1., .95, .9) : i % 3 == 1 ? pink : cyan;
                float on = .25 + .75 * M * (.55 + .45 * step(.3, fract(BIX * .5 + fi * .29)));
                st += bcol * beam * on * (.8 + .7 * B + 1.5 * strobe);
                st += vec3(1.) * (exp(-length(rp) * 9.) * 1.5 + exp(-length(rp) * 2.5) * .25) * (.5 + .7 * B);
            }
            // Blinder: grosse weisse Lampen, blitzen auf dem Schlag und beim Drop
            for (int i = 0; i < 4; i++) {
                vec2 bp = vec2((float(i) - 1.5) * 2.3, 3.62);
                float bl = M * (exp(-ph * 9.) * step(.5, mod(BIX + float(i), 2.)) * .7 + drop);
                st += vec3(1., .92, .8) * (exp(-length(q - bp) * 6.) * 2. + exp(-length(q - bp) * 1.2) * .3) * bl;
            }
            // ---- Laser
            if (M > .02) {
                for (int i = 0; i < 8; i++) {
                    float fi = float(i);
                    float ang = (fi - 3.5) * .22 + .45 * sin(t * 1.3 + fi * .8 + floor(BIX / 2.) * .9);
                    vec2 dir = vec2(sin(ang), cos(ang));
                    vec2 rp = q - vec2(0., -.7);
                    float along = dot(rp, dir), perp = abs(dot(rp, vec2(dir.y, -dir.x)));
                    float l = exp(-perp / .012) * step(.3, along) * step(.45, fract(BIX * .5 + fi * .37));
                    st += (i % 2 == 0 ? vec3(.3, 1., .5) : vec3(.5, .4, 1.)) * l * M * 1.1;
                }
            }
            st += hue * smoothstep(.3, .8, haze) * (.04 + .12 * B) * smoothstep(1.5, -1.5, q.y);
            st += vec3(1.) * strobe * .18 + vec3(1., .5, .15) * drop * .12;   // Strobo + Feuerschein
            col = mix(col, st, fade);
        }

        if (LAYER < .5 || LAYER > 1.5) {
            float fk = drop * (QLOW > .5 ? .6 : 1.);
            // ---- 3D-DJ
            vec3 rd = vec3(dot(d, R), dot(d, U), -dot(d, C));
            vec3 ro = vec3(0., 0., D);
            vec3 oc = ro - vec3(0., .45, .45);
            float bb = dot(oc, rd), cc = dot(oc, oc) - 3.8 * 3.8, disc = bb * bb - cc;
            if (disc > 0.) {
                djPose(t);
                float tt = max(-bb - sqrt(disc), 0.), tmax = -bb + sqrt(disc);
                vec2 hit = vec2(-1.);
                int steps = QLOW > .5 ? 28 : 48;
                for (int i = 0; i < steps; i++) {
                    vec2 h = djMap(ro + rd * tt);
                    if (h.x < .003 * tt) { hit = vec2(tt, h.y); break; }
                    tt += h.x * .92;
                    if (tt > tmax) break;
                }
                if (hit.x > 0.) {
                    vec3 p = ro + rd * hit.x, n = djNormal(p);
                    float mat = hit.y;
                    vec3 v = -rd;
                    vec3 Lk = normalize(vec3(.35, .75, .8));
                    vec3 Lp = normalize(vec3(-.8, .4 + .2 * sin(t * .7), .4)), Lc = normalize(vec3(.8, .4 + .2 * cos(t * .6), .4));
                    float sh = QLOW > .5 ? 1. : djShadow(p + n * .01, Lk);
                    float dif = clamp(dot(n, Lk), 0., 1.) * sh;
                    float dp = clamp(dot(n, Lp), 0., 1.), dc = clamp(dot(n, Lc), 0., 1.);
                    float fres = pow(1. - clamp(dot(n, v), 0., 1.), 3.);
                    vec3 hv = normalize(Lk + v);
                    float occ = clamp(.5 + djMap(p + n * .2).x / .4, 0., 1.);
                    vec3 alb = vec3(.1);
                    float gloss = .25, rough = 24., metal = 0.;
                    vec3 emis = vec3(0.);
                    vec3 hp = p - jHead;
                    if (mat < 1.5) { alb = vec3(.68, .49, .39); gloss = .3; rough = 18.; }
                    else if (mat < 2.5) {
                        alb = STYLE < .5 ? vec3(.86, .87, .9) : STYLE > 3.5 ? vec3(.62, .07, .09) : STYLE > 2.5 ? vec3(.05, .05, .06) : vec3(.88);
                        gloss = .35;
                        vec3 lp = p - jChest;
                        if ((STYLE < .5 || (STYLE > 2.5 && STYLE < 3.5)) && lp.z > .2 && abs(lp.x) < .42 && lp.y > -.35 && lp.y < .3) {
                            float bx = floor((lp.x + .42) / .12);
                            float lvl = (.25 + .75 * fract(sin(bx * 12.9 + floor(t * 6.) * 3.1) * 43758.5)) * (.35 + .65 * M) * (.6 + .6 * B);
                            float yy = (lp.y + .35) / .65;
                            if (yy < lvl && fract((lp.x + .42) / .12) < .7 && fract(yy * 8.) < .75) emis = mix(cyan, pink, yy) * 1.5;
                        }
                        if (STYLE > 2.5 && STYLE < 3.5) emis += hue * smoothstep(.04, 0., abs(abs(lp.x) - .5)) * 1.2; // Neon-Kanten
                    }
                    else if (mat < 3.5) { alb = vec3(.06); gloss = 1.2; rough = 60.; emis = hue * smoothstep(.14, .18, abs(hp.x)) * smoothstep(.22, .16, length(hp.yz - vec2(-.04, 0.))) * (.5 + 1.3 * B); }
                    else if (mat < 4.5) {
                        alb = vec3(.035, .035, .05); gloss = .6; rough = 40.;
                        if (n.z > .7) {
                            emis += mix(vec3(.25, .3, 1.), vec3(.8, .2, 1.), .5 + .5 * sin(p.x * 1.5 - t * 3.)) * smoothstep(.05, 0., abs(p.y + .82) - .04) * (.9 + .8 * B);
                            vec2 dm = vec2(p.x * 6., (p.y + 1.85) * 6.);
                            float lv = (.3 + .7 * fract(sin(floor(dm.x) * 7.1 + floor(t * 8.) * 1.7) * 43758.5)) * (.3 + .7 * M) * (.5 + .7 * B) * 5.;
                            float dot0 = smoothstep(.38, .25, length(fract(dm) - .5)) * step(floor(dm.y), lv) * step(0., dm.y) * step(dm.y, 5.) * step(abs(p.x), 2.4);
                            emis += mix(cyan, pink, dm.y / 5.) * dot0 * .9;
                        }
                    }
                    else if (mat < 5.5) {
                        vec2 pc = vec2(p.x - sign(p.x) * 1.45, p.z - 1.);
                        float rr = length(pc), aa = atan(pc.y, pc.x) + t * (1.5 + 3. * M);
                        alb = vec3(.025) + vec3(.035) * smoothstep(.3, .7, abs(fract(rr * 9.) - .5) * 2.);
                        emis = hue * smoothstep(.14, .12, rr) * .9 + vec3(1.) * smoothstep(.04, 0., abs(fract(aa / 6.2832) - .5) - .01) * step(.15, rr) * .3;
                        emis += cyan * smoothstep(.02, 0., abs(rr - .49)) * (.4 + .8 * B);
                        gloss = 1.; rough = 50.;
                    }
                    else if (mat < 6.5) {
                        alb = vec3(.06);
                        vec2 bt = fract(vec2(p.x * 6., p.z * 6.)) - .5;
                        emis = mix(cyan, pink, step(0., p.x)) * smoothstep(.22, .12, length(bt)) * (.4 + .8 * step(.5, fract(BIX * .5 + floor(p.x * 6.) * .3)));
                    }
                    else if (mat < 7.5) { alb = vec3(.01); gloss = 2.; rough = 90.; emis = mix(wallTint, vec3(1.), .2) * pow(clamp(n.y * .5 + .5, 0., 1.), 3.) * .7; }
                    else if (mat < 8.5) { alb = STYLE > 3.5 ? vec3(.86, .8, .66) : vec3(.07, .07, .08); gloss = .2; }
                    else if (mat < 9.5) { alb = vec3(.95, .7, .28); gloss = 2.2; rough = 90.; metal = 1.; }
                    else if (mat < 10.5) { alb = vec3(.09, .06, .05); gloss = .1; }
                    else if (mat < 11.5) {
                        alb = vec3(.16, .16, .19); gloss = 1.2; rough = 50.;
                        emis = hue * smoothstep(.07, .05, length(vec2(p.x, p.y + .45))) * step(.5, n.z) * (.6 + .8 * B);
                    }
                    else if (mat < 12.5) { alb = vec3(.6, .62, .68); gloss = 2.; rough = 70.; metal = 1.; }
                    else if (mat < 13.5) {   // Robo-Visier: Augen, auf dem Schlag Equalizer
                        alb = vec3(.01); gloss = 2.; rough = 90.;
                        vec2 vu = vec2(hp.x / .3, (hp.y - .02) / .12);
                        float eyes = smoothstep(.25, .15, length(vec2(abs(vu.x) - .45, vu.y * .9)));
                        float eq = step(abs(vu.y) * 1.2, (.3 + .7 * fract(sin(floor(vu.x * 5.) * 9.1 + floor(t * 8.)) * 437.5)) * B * 1.5) * step(.2, fract(vu.x * 5.));
                        emis = mix(cyan, pink, .5 + .5 * vu.x) * max(eyes * (1. - B * .8), eq) * 1.6 + vec3(.2, .6, 1.) * step(.6, length(hp - vec3(.3, .66, 0.)) * 10.) * 0.;
                        if (length(hp - vec3(.3, .66, 0.)) < .08) emis = hue * (1. + B);
                    }
                    else if (mat < 14.5) { alb = vec3(.9, .9, .92); gloss = .9; rough = 40.; }
                    else if (mat < 15.5) { alb = vec3(.25, .17, .05); gloss = 2.5; rough = 120.; metal = 1.; emis = wallTint * fres * .9 + mix(wallTint, vec3(1.), .4) * pow(clamp(n.y * .5 + .6, 0., 1.), 4.) * .45; }
                    else {   // LED-Wuerfel: Pixel-Gesicht vorne, Farbverlauf an den Seiten
                        alb = vec3(.02); gloss = 1.4; rough = 60.;
                        vec2 cell = floor((hp.xy / .84 + .5) * 8.);
                        vec2 cf = fract((hp.xy / .84 + .5) * 8.);
                        float px0 = smoothstep(.5, .4, max(abs(cf.x - .5), abs(cf.y - .5)));
                        if (n.z > .7) {
                            float face = 0.;
                            bool happy = B > .4;
                            if (cell.y == 5. && (cell.x == 2. || cell.x == 5.)) face = 1.;
                            if (happy && cell.y == 6. && (cell.x == 1. || cell.x == 3. || cell.x == 4. || cell.x == 6.)) face = 1.;
                            if (cell.y == 2. && cell.x >= 2. && cell.x <= 5.) face = 1.;
                            if (cell.y == 3. && (cell.x == 1. || cell.x == 6.)) face = 1.;
                            emis = mix(hue * .15, vec3(1.), face) * px0 * (1.1 + .6 * B);
                        } else {
                            emis = (.5 + .5 * cos(6.2831 * (vec3(0., .33, .67) + (cell.x + cell.y) * .07 + t * .3))) * px0 * (.5 + .7 * B);
                        }
                    }
                    vec3 key = vec3(1., .93, .86) * (.8 + .45 * B);
                    vec3 fireL = vec3(1., .5, .15) * fk * 1.2;
                    vec3 spec = mix(key, key * alb, metal);
                    vec3 lit = alb * (1. - metal * .6) * (.16 + key * dif * 1.2 + pink * dp * (.25 + .5 * M) + cyan * dc * (.25 + .5 * M) + fireL * clamp(n.z * .5 + .5, 0., 1.)) * occ;
                    lit += spec * pow(clamp(dot(n, hv), 0., 1.), rough) * gloss * sh * .8;
                    lit += metal * mix(wallTint, alb, .5) * (.25 + .6 * fres) * occ;   // Spiegelung der LED-Wand
                    lit += mix(wallTint, mix(pink, cyan, step(0., n.x)), .5) * fres * (.5 + .8 * M) * (.7 + .5 * B);
                    lit += emis;
                    col = mix(col, lit, fade);
                    FG_A = fade;
                }
            }
            // ---- Feuer beim Drop (vor dem Pult)
            vec4 fire = djFire(q, t, fk);
            if (fire.a > .002) {
                col = col * (1. - fire.a * fade) + fire.rgb * fade;
                FG_A = max(FG_A, fire.a * fade);
            }
            // ---- Crowd vorne
            if (q.y < -1.6) {
                float sil = 1e3;
                for (int i = 0; i < 18; i++) {
                    float fi = float(i);
                    float x0 = -7.2 + fi * .84 + .3 * sh21(vec2(fi, 2.));
                    float jp = (.12 + .1 * sh21(vec2(fi, 5.))) * bnc + drop * .25 * bnc;
                    float sway = .25 * sin(t * 1.5 + fi * 1.7) * (.3 + .7 * M);
                    vec2 hd = vec2(x0, -3.25 + jp);
                    sil = min(sil, length((q - hd) * vec2(1., .9)) - .38);
                    sil = min(sil, fBox(q - vec2(x0, -3.9 + jp), vec2(.55, .4), .25));
                    if (sh21(vec2(fi, 9.)) > .3 || drop > .3) {
                        vec2 hand = vec2(x0 + .35 + sway, -1.95 + jp + .15 * sh21(vec2(fi, 4.)) - (1. - M) * .7);
                        sil = min(sil, fCap(q, vec2(x0 + .3, -3.45 + jp), hand, .1));
                        sil = min(sil, length(q - hand) - .15);
                        for (int f = 0; f < 3; f++) sil = min(sil, fCap(q, hand, hand + vec2(-.12 + float(f) * .12, .22), .035));
                        if (sh21(vec2(fi, 13.)) > .7) {
                            float ph0 = exp(-length(q - hand - vec2(0., .2)) * 18.) * .8 * fade;
                            col += vec3(.9, .95, 1.) * ph0;
                            FG_A = max(FG_A, ph0);
                        }
                    }
                }
                float inside = smoothstep(.02, -.01, sil) * fade;
                float edge = exp(-max(sil, 0.) * 25.) * step(0., sil) * .25 * fade * (.4 + M);
                col = mix(col, vec3(.01, .005, .02) + vec3(1., .45, .12) * fk * .08, inside);
                col += mix(pink, cyan, .5 + .5 * sin(q.x)) * edge;
                FG_A = max(FG_A, max(inside, edge));
            }
        }
    }
    if (LAYER < 1.5) col *= 1. - clamp(-el / 8., 0., 1.) * .5;
    return col;
}

vec3 freshSky(vec3 d, int id, float t) {
    if (id == 8) return warSky(d, t);
    if (id == 9) return djSky(d, t);
    return calmSky(d, id, t);
}
