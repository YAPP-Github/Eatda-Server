resource "aws_iam_group" "timeeat_iam_group" {
  name = var.group_name
}

resource "aws_iam_user" "timeeat_iam_user" {
  for_each = toset(var.user_names)

  name = each.value
  tags = var.tags
}

resource "aws_iam_group_membership" "timeeat_membership" {
  name  = "${var.group_name}-membership"
  users = [aws_iam_user.timeeat_iam_user.name]
  group = aws_iam_group.timeeat_iam_group.name
}

resource "aws_iam_group_policy_attachment" "timeeat_policies" {
  for_each = toset(var.policy_arns)

  group      = aws_iam_group.timeeat_iam_group.name
  policy_arn = each.value
}

resource "aws_iam_policy" "deny_if_no_mfa" {
  count  = var.enable_mfa_enforcement ? 1 : 0
  name   = "${var.group_name}-deny-no-mfa"
  policy = data.aws_iam_policy_document.deny_without_mfa.json
}

resource "aws_iam_group_policy_attachment" "deny_no_mfa_attach" {
  count      = var.enable_mfa_enforcement ? 1 : 0
  group      = aws_iam_group.timeeat_iam_group.name
  policy_arn = aws_iam_policy.deny_if_no_mfa[0].arn
}

data "aws_iam_policy_document" "deny_without_mfa" {
  statement {
    effect = "Deny"
    actions = ["*"]
    resources = ["*"]

    condition {
      test     = "BoolIfExists"
      variable = "aws:MultiFactorAuthPresent"
      values = ["false"]
    }
  }
}
