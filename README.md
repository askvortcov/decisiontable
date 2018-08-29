# Tree-based-domain decision table
[![Build Status](https://travis-ci.com/askvortcov/decisiontable.svg?branch=master)](https://travis-ci.com/askvortcov/decisiontable)

Decision tables are a concise visual representation for specifying which actions to perform depending on given 
conditions [wikipedia](https://en.wikipedia.org/wiki/Decision_table). 

outlook	| temperature   | precipitation	| wind      | outcome
:------:|:-------------:|:-------------:|:---------:|-------:
any	    | unbearable	| any           | any	    | don't play
overcast| any	        | any	        | stormy	| don't play
sunny	| positive	    | any	        | calm	    | play
any	    | positive	    |  showers	    | any	    | don't play
_Default_                                           | **play**


The columns in such table represent decision domains. 
Cells represent a predicate about such domain. This should be a node in the domain representation tree:


<table>
<thead><tr>
    <th>    outlooks        </th>
    <th>    temperatures    </th>
    <th>    precipitations  </th>
    <th>    winds           </th>
</tr></thead>
<tbody><tr><td>

- ANY 
  * Clear
  * Cloudy
  * Overcast
</td><td>
  
- ANY                   
  * 0˚
  * Negative
    - Fresh _(>-10˚)_
      + -1˚
      + -2˚
      + ...     
    -  freezing _(-10˚&#8209;&nbsp;-20˚)_
    -  unbearable _(<20˚)_
  * Positive
    - cold _(<+10˚)_
    - comfortable _(+10˚&#8209;&nbsp;+20˚)_
    - warm _(+10˚&#8209;&nbsp;+20˚)_
    - hot _(+20˚&#8209;&nbsp;+30˚)_
    - smelting _(>+30˚)_
</td><td>

- ANY 
  * Rainy
    - SHOWERS

        + MODERATE
        + HEAVY
        + FLOOD  
    -  LIGHT           
  * SNOWY
    - LIGHT
    - MODERATE
    - STORM
  * NONE
</td><td>

- ANY
  * Calmy
    - Calm
    - Light air
  * Breeze
    - Light
    - Gentle
    - Moderate
    - Fresh
    - Strong
  * gale
    - moderate
    - fresh
    - Strong
  * Stormy
    - whole gale
    - Violent storm
    - Hurricane force
</td></tr></tbody></table>


So we can always test with some exact (leaf in the tree) conditions:

1. overcast, +17˚C, light showers, light breeze – results in "don't play" since the 4th line
2. sunny,  +5˚C, heavy showers, calm – results in "play" despite line 4, since matches line 3 
