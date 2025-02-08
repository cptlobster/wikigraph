for f in wp-data/*.xml*; do
  TAIL=$(tail -n1 $f)
  if [[ $TAIL != "</mediawiki>" ]]; then
    echo "</mediawiki>" >> $f
    echo "Added closing tag to $f"
  else
    echo "$f has closing tag. No action taken."
  fi
done